package com.nkhoang.gae.action;

import com.nkhoang.gae.manager.GoldManager;
import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.utils.DateConverter;
import com.nkhoang.gae.utils.WebUtils;
import com.nkhoang.gae.view.constant.ViewConstant;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/service/gold")
public class ExchangeRateAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateAction.class);
    @Autowired
    private GoldManager goldService;

    @RequestMapping("/" + ViewConstant.EXCHANGE_RATE_REQUEST)
    public void update(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("Starting to update Exchange rate...");
        List<Currency> listCurrencies = getExchangeRate();

        for (int i = 0; i < listCurrencies.size(); i++) {
            Currency unit = listCurrencies.get(i);

            Currency latest = goldService.getExchangeRate(unit.getCurrency());
            if (latest == null || (!latest.getPriceBuy().equals(unit.getPriceBuy()) && !latest.getPriceSell().equals(unit.getPriceSell()))) {
                LOGGER.debug("Saving unit:" + unit.toString());
                goldService.save(unit);
            }
        }

        response.setContentType("application/json");
    }

    private List<Currency> getExchangeRate() {
        List<Currency> listExchangeRate = new ArrayList<Currency>(0);
        Source source = null;
        try {
            source = WebUtils.retrieveWebContent("http://www.sjc.com.vn/ajax_currency.php");
        } catch (Exception ex) {
            LOGGER.error("Could not parse URL : http://www.sjc.com.vn/ajax_currency.php", ex);
        }

        if (source != null) {
            List<Element> timeElements = source.getAllElementsByClass("text_tgvang_mota");
            String timeString = timeElements.get(0).getContent().toString();
            // create a new Currency object.
            Currency currency = new Currency();
            // process time
            Pattern p = Pattern.compile("\\d{2}:\\d{2} (AM|PM) \\d{2}\\/\\d{2}\\/\\d{4}");
            Matcher m = p.matcher(timeString);
            String currencyTime = "";
            if (m.find()) {
                currencyTime = m.group(0);
            }

            Date currencyDate = null;

            // set to currency
            try {
                currencyDate = DateConverter.convertFromStringToken(currencyTime, DateConverter.defaultCurrencyDateFormat);
                LOGGER.info("Time of the exchange rate: " + currencyTime + " date " + currencyDate.toString());
            } catch (ParseException parseEx) {
                LOGGER.error("Could not parse time for Currency.", parseEx);
                currency = null;
            }
            if (currency != null) {
                // process more information
                List<Element> otherElements = source.getAllElementsByClass("text_tgvang_chitiet");
                Iterator<Element> otherElementIter = otherElements.iterator();
                int counter = 0;
                while (otherElementIter.hasNext()) {
                    currency = new Currency();
                    currency.setTime(currencyDate.getTime());
                    Element element = otherElementIter.next();
                    currency.setCurrency(element.getTextExtractor().toString());
                    counter++;
                    element = otherElementIter.next();
                    currency.setPriceBuy(Float.parseFloat(element.getContent().toString()));
                    counter++;
                    element = otherElementIter.next();
                    currency.setPriceSell(Float.parseFloat(element.getContent().toString()));
                    counter++;
                    otherElementIter.next();
                    LOGGER.debug(currency.toString());
                    listExchangeRate.add(currency);
                }
            }
        }
        return listExchangeRate;
    }


    public GoldManager getGoldService() {
        return goldService;
    }

    public void setGoldService(GoldManager goldService) {
        this.goldService = goldService;
    }
}
