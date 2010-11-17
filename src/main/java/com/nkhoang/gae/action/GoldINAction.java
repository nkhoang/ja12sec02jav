package com.nkhoang.gae.action;

import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.GoldManager;
import com.nkhoang.gae.model.GoldPrice;
import com.nkhoang.gae.utils.WebUtils;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/service/gold")
public class GoldINAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldINAction.class);
    @Autowired
    private GoldManager goldService;

    @RequestMapping("/" + ViewConstant.GOLD_IN_UPDATE_REQUEST)
    public void update(HttpServletResponse response) {
        LOGGER.debug("Starting to update Gold International...");
        GoldPrice goldPrice = getInternationalGoldPrice();
        LOGGER.debug("Gold Price retrieved: " + goldPrice.toString());
        goldService.save(goldPrice);

        response.setContentType("text/html");
    }

    @RequestMapping("/" + ViewConstant.GOLD_IN_DATA_REQUEST)
    public ModelAndView retrieveGoldIN() {
        LOGGER.debug("Starting to retrieve gold data [USD] to requester...");

        List<GoldPrice> list = goldService.getAllGoldPrice("USD");
        ModelAndView mav = new ModelAndView();
        JSONView view = new JSONView();
        mav.setView(view);
        mav.addObject("data", list);
         // construct data
        List<String> attrs = new ArrayList<String>();
        attrs.addAll(Arrays.asList(GoldPrice.SKIP_FIELDS));
        mav.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);
        return mav;
    }

    private GoldPrice getInternationalGoldPrice() {
        GoldPrice price = new GoldPrice();
        Source source = null;
        try {
            source = WebUtils.retrieveWebContent("http://giavang.net/resources/pricetable2a.asp");
        } catch (IOException ioe) {
            LOGGER.error("Could not parse URL: [http://giavang.net/resources/pricetable2a.asp]", ioe);
        }

        if (source != null) {
            List<Element> trElements = source.getAllElementsByClass("row1");
            Element trElement = trElements.get(0);

            List<Element> tdElements = trElement.getChildElements();
            // get the 2nd td
            Element priceElement = tdElements.get(1);

            String priceString = priceElement.getTextExtractor().toString();
            LOGGER.debug("International Gold Price: " + priceString);
            // process string
            Calendar calendar = GregorianCalendar.getInstance();
            LOGGER.info(calendar.getTimeZone().getDisplayName());
            Date currentDate = calendar.getTime();
            price.setTime(currentDate.getTime());
            price.setCurrency("USD");
            // Using regex to get price value.
            Pattern p = Pattern.compile("\\d+.\\d+");
            Matcher m = p.matcher(priceString.split("/")[0]);
            if (m.find()) {
                price.setPriceSell(Float.parseFloat(m.group(0)));
            }
            m = p.matcher(priceString.split("/")[1]);
            if (m.find()) {
                price.setPriceBuy(Float.parseFloat(m.group(0)));
            }
        }
        return price;
    }

    public GoldManager getGoldService() {
        return goldService;
    }

    public void setGoldService(GoldManager goldService) {
        this.goldService = goldService;
    }
}
