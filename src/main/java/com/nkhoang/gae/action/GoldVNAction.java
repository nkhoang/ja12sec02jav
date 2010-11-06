package com.nkhoang.gae.action;

import com.nkhoang.gae.manager.GoldManager;
import com.nkhoang.gae.model.GoldPrice;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/service/gold")
public class GoldVNAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldVNAction.class);
    @Autowired
    private GoldManager goldService;

    @RequestMapping("/" + ViewConstant.GOLD_VN_UPDATE_REQUEST)
    public void update() {
        LOGGER.debug("Starting to update Gold Vietnam...");
        List<GoldPrice> listGoldPrice = getVNGoldData("http://www.sjc.com.vn/chart/data.csv");
        LOGGER.debug("Total result: " + listGoldPrice.size());
        int existingCounter = 0;
        for (int i = 0; i < listGoldPrice.size(); i++) {
            GoldPrice unit = listGoldPrice.get(i);
            if (!goldService.check(unit)) {
                LOGGER.debug("Saving :" + unit.toString());
                goldService.save(unit);
            } else {
                existingCounter++;
                if (existingCounter == 10) {
                    break;
                }
            }
        }
    }

    // http://www.sjc.com.vn/chart/data.csv
    private List<GoldPrice> getVNGoldData(String websiteURL) {
        List<GoldPrice> listGoldPrice = new ArrayList<GoldPrice>(0);
        BufferedInputStream bis = null;
        try {
            // open connection
            URL url = new URL(websiteURL);
            URLConnection connection = url.openConnection();
            bis = new BufferedInputStream(connection.getInputStream());


            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String strLine;

            StringTokenizer st;

            int lineNumber = 0, tokenNumber = 0;
            //read comma separated file line by line
            while ((strLine = br.readLine()) != null) {
                lineNumber++;
                //break comma separated line using ","
                st = new StringTokenizer(strLine, ",");
                while (st.hasMoreTokens()) {
                    //display csv values
                    tokenNumber++;
                    String goldToken = st.nextToken();
                    // starting to create gold object
                    GoldPrice goldUnit = createGoldObject(goldToken);
                    listGoldPrice.add(goldUnit);
                }
                //reset token number
                tokenNumber = 0;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get VN gold data from URL: [" + websiteURL + "].", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ioe) {
                    LOGGER.error("Could not close stream", ioe);
                }
            }
        }
        return listGoldPrice;
    }

    private GoldPrice createGoldObject(String tokenString) {
        GoldPrice price = null;
        // parse String token
        String[] info = tokenString.split(";");
        if (info.length != 3) {
            LOGGER.error("Not enough information to create gold price object.");
        } else {
            price = new GoldPrice();
            Date priceDate = null;
            try {
                priceDate = DateConverter.convertFromStringToken(info[0], DateConverter.defaultGoldDateFormat);
            } catch (ParseException parseEx) {
                LOGGER.error("Could not parse date.", parseEx);
                price = null;
            }
            if (price != null) {
                price.setCurrency("VND");
                price.setTime(priceDate);
                price.setPriceBuy(Float.parseFloat(info[1]));
                price.setPriceSell(Float.parseFloat(info[2]));
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
