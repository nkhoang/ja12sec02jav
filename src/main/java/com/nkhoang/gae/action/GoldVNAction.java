package com.nkhoang.gae.action;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.nkhoang.gae.manager.GoldManager;
import com.nkhoang.gae.model.GoldPrice;
import com.nkhoang.gae.utils.DateConverter;
import com.nkhoang.gae.view.constant.ViewConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

@Controller
@RequestMapping("/service/gold")
public class GoldVNAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldVNAction.class);
    @Autowired
    private GoldManager goldService;
    private static final String UPDATE_ALL_STARTING_INDEX_PARAM = "startingIndex";
    private static final int GOOGLE_APPENGINE_EXPIRE_INTERVAL = 16384;

    @RequestMapping("/" + ViewConstant.GOLD_VN_UPDATE_REQUEST)
    public void update(@RequestParam(UPDATE_ALL_STARTING_INDEX_PARAM) String startingIndex, HttpServletResponse response) {
        LOGGER.debug("Starting to update Gold Vietnam...");
        List<GoldPrice> listGoldPrice = getVNGoldData("http://www.sjc.com.vn/chart/data.csv");
        LOGGER.debug("Total result: " + listGoldPrice.size());

        final long start = System.currentTimeMillis();
        boolean shouldNotContinue = false;
        int i = 0;
        if (StringUtils.isNotEmpty(startingIndex))
        {
            try {
                i = Integer.parseInt(startingIndex);
            } catch (Exception e) {
                LOGGER.error("Could not parse request param for Gold price update all.", e);
            }
        }
        while (System.currentTimeMillis() - start < GOOGLE_APPENGINE_EXPIRE_INTERVAL) {
            GoldPrice unit = listGoldPrice.get(i);
            if (!goldService.check(unit)) {
                LOGGER.debug("Saving :" + unit.toString());
                goldService.save(unit);
            }else {
                shouldNotContinue = true;
                // just break if object was saved. Save CPU time.
                break;
            }
            i++;
            if (i == listGoldPrice.size()) {
                break;
            }
        }
        LOGGER.info("Have saved: " + i + " item to GoldPrice table.");
        if (!shouldNotContinue){
            QueueFactory.getDefaultQueue().add(url("/service/gold/" + ViewConstant.GOLD_VN_UPDATE_REQUEST + ".html?" + UPDATE_ALL_STARTING_INDEX_PARAM + "=" + i).method(TaskOptions.Method.GET));    
        }

        response.setContentType("application/json");
    }

    @RequestMapping("/" + ViewConstant.GOLD_VN_UPDATE_ALL_REQUEST)
    public void updateAll(@RequestParam(UPDATE_ALL_STARTING_INDEX_PARAM) String startingIndex, HttpServletResponse response) {
        LOGGER.debug("Starting to update Gold Vietnam width index = " + startingIndex + " ...");
        List<GoldPrice> listGoldPrice = getVNGoldData("http://www.sjc.com.vn/chart/data.csv");
        LOGGER.debug("Total result: " + listGoldPrice.size());

        final long start = System.currentTimeMillis();

        // check request parameter - do not required.
        int i = 0;
        if (StringUtils.isNotEmpty(startingIndex))
        {
            try {
                i = Integer.parseInt(startingIndex);
            } catch (Exception e) {
                LOGGER.error("Could not parse request param for Gold price update all.", e);
            }
        }

        int itemCount = 0;
        while (System.currentTimeMillis() - start < GOOGLE_APPENGINE_EXPIRE_INTERVAL) {
            if (i == listGoldPrice.size()) {
                break;
            }
            GoldPrice unit = listGoldPrice.get(i);
            if (!goldService.check(unit)) {
                LOGGER.debug("Saving :" + unit.toString());
                goldService.save(unit);
            }
            i++;

            itemCount++;
        }

        LOGGER.info("Have saved: " + itemCount + " item to GoldPrice table.");

        if (listGoldPrice.size() - 1 <= i) {
            LOGGER.info("Nothing to save");
        } else {
            LOGGER.info("Posting to Queue");
            QueueFactory.getDefaultQueue().add(url("/service/gold/" + ViewConstant.GOLD_VN_UPDATE_ALL_REQUEST + ".html?" + UPDATE_ALL_STARTING_INDEX_PARAM + "=" + i).method(TaskOptions.Method.GET));
        }

        response.setContentType("application/json");
        
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

    /**
     * Used to create Gold object.
     * @param tokenString token string to get data from.
     * @return a GoldPrice obj.
     */
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
                price.setTime(priceDate.getTime());
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
