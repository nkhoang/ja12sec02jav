package com.nkhoang.gae.action;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.nkhoang.gae.manager.GoldManager;
import com.nkhoang.gae.model.GoldPrice;
import com.nkhoang.gae.model.GoldPriceSortByTime;
import com.nkhoang.gae.utils.DateConverter;
import com.nkhoang.gae.view.XMLView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;


@Controller
@RequestMapping("/data")
public class XMLDataAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLDataAction.class);
    @Autowired
    private GoldManager goldService;
    // Request params.
    private static final String TASKCOUNT_PARAM = "taskcount";
    private static final String CLEAR_ALL_KIND_PARAM = "kind";

    /**
     * Clear all data from a kind.
     *
     * @param kind            can be any kind of object in String. (e.g. GoldPrice)
     * @param taskCountString starting counter.
     * @param request         HttpServletRequest object.
     * @param response        HttpServletResponse object.
     * @return view name.
     */
    @RequestMapping(value = "/" + ViewConstant.CLEAR_ALL_REQUEST, method = RequestMethod.GET)
    public String clearAll(@RequestParam(CLEAR_ALL_KIND_PARAM) String kind, @RequestParam(TASKCOUNT_PARAM) String taskCountString, HttpServletRequest request, HttpServletResponse response) {
        try {
            // check params.
            if (StringUtils.isEmpty(kind) || StringUtils.isEmpty(taskCountString)) {
                response.getWriter().write("<h1>Bad Request</h1>");
            }
            LOGGER.info("*** deleting entities form " + kind);
            final long start = System.currentTimeMillis();
            int deleted_count = 0;
            boolean is_finished = false;
            final DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
            while (System.currentTimeMillis() - start < 16384) {
                final Query query = new Query(kind);
                query.setKeysOnly();
                final ArrayList<Key> keys = new ArrayList<Key>();
                for (final Entity entity : dss.prepare(query).asIterable(FetchOptions.Builder.withLimit(128))) {
                    keys.add(entity.getKey());
                }
                keys.trimToSize();
                if (keys.size() == 0) {
                    is_finished = true;
                    break;
                }
                while (System.currentTimeMillis() - start < 16384) {
                    try {
                        dss.delete(keys);
                        deleted_count += keys.size();
                        break;
                    } catch (Throwable ignore) {
                        continue;
                    }
                }
            }
            LOGGER.info("*** deleted " + deleted_count + " entities form " + kind);
            if (is_finished) {
                LOGGER.info("*** deletion job for " + kind + " is completed.");
            } else {
                final int taskCount;
                final String tcs = taskCountString;
                if (tcs == null) {
                    taskCount = 0;
                } else {
                    taskCount = Integer.parseInt(tcs) + 1;
                }
                QueueFactory.getDefaultQueue().add(url("/data/clearAll.html?" + CLEAR_ALL_KIND_PARAM + "=" + kind + "&" + TASKCOUNT_PARAM + "=" + taskCount).method(TaskOptions.Method.GET));

                LOGGER.info("*** deletion task # " + taskCount + " for " + kind + " is queued.");
            }
        } catch (Exception e) {
            LOGGER.error("Error when deleting " + CLEAR_ALL_KIND_PARAM + ":" + kind, e);
        }
        // just return as a normal HTML.
        return "html";
    }


    /**
     * Return XML to be rendered as a chart.
     * <p/>
     * ?fromDate=2008-01-0%2016:00&toDate=2008-05-15%2015:00
     *
     * @param fromDateString in default Gold price format.
     * @param toDateString   in default Gold price format.
     * @return
     */
    @RequestMapping(value = "/" + ViewConstant.XML_DATA_CHART_REQUEST)
    public ModelAndView retrieveXMLData(@RequestParam("fromDate") String fromDateString, @RequestParam("toDate") String toDateString) {
        // test get price range
        ModelAndView mav = new ModelAndView();
        try {
            if (StringUtils.isNotEmpty(fromDateString) && StringUtils.isNotEmpty(toDateString)) {
                goldService.getAllGoldPrice();

                Date fromDate = DateConverter.convertFromStringToken(fromDateString, DateConverter.defaultGoldDateFormat);
                Date toDate = DateConverter.convertFromStringToken(toDateString, DateConverter.defaultGoldDateFormat);

                Map<String, List<GoldPrice>> goldMap = buildGoldPrice4Chart(fromDate, toDate);

                List<GoldPrice> vnList = goldMap.get("VND");
                List<GoldPrice> inList = goldMap.get("USD");

                LOGGER.info("Gold Price vnList: " + vnList.size());

                InputStream is = this.getClass().getResourceAsStream("/MSLine.xml");

                if (is == null) {
                    LOGGER.info("Could not load resources.");
                }

                VTDGen vg = new VTDGen(); // Instantiate VTDGen
                StringWriter writer = new StringWriter();
                IOUtils.copy(is, writer);
                String theString = writer.toString();
                vg.setDoc(theString.getBytes());
                vg.parse(true);

                XMLModifier xm = new XMLModifier(); //Instantiate XMLModifier
                LOGGER.info("Starting to parse XML");
                VTDNav vn = vg.getNav();

                xm.bind(vn);

                AutoPilot ap = new AutoPilot(vn);

                ap.selectXPath("/chart/categories");
                int i = -1;
                while ((i = ap.evalXPath()) != -1) {
                    String categoryTag = "";
                    for (GoldPrice p : vnList) {
                        Date d = new Date();
                        d.setTime(p.getTime());
                        categoryTag += "\n\t<category label='" + DateConverter.parseDate(d, DateConverter.defaultGoldDateFormat) + "'/>";

                    }
                    xm.insertAfterHead(categoryTag);
                }
                ap.selectXPath("/chart/dataset[@seriesName='VN']");
                i = -1;
                while ((i = ap.evalXPath()) != -1) {
                    String setTag = "";
                    for (GoldPrice p : vnList) {
                        setTag += "\n\t<set value='" + p.getPriceBuy() + "'/>";

                    }
                    xm.insertAfterHead(setTag);

                }

                ap.selectXPath("/chart/dataset[@seriesName='International']");
                i = -1;

                while ((i = ap.evalXPath()) != -1) {
                    String setTag = "";
                    for (GoldPrice p : inList) {
                        setTag += "\n\t<set value='" + p.getPriceBuy() + "'/>";

                    }
                    xm.insertAfterHead(setTag);
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                xm.output(bos);

                View xmlView = new XMLView();
                mav.setView(xmlView);
                mav.addObject("data", bos.toString());
            } else {
                mav.setViewName(ViewConstant.HTML_VIEW);
            }

        } catch (Exception ex) {
            LOGGER.error("Could not parse XML file", ex);
            mav.setViewName(ViewConstant.HTML_VIEW);
        }


        return mav;
    }

    private Map<String, List<GoldPrice>> buildGoldPrice4Chart(Date fromDate, Date toDate) {
        Map<String, List<GoldPrice>> result = new HashMap<String, List<GoldPrice>>();

        List<GoldPrice> vnList = new ArrayList(0);
        List<GoldPrice> inList = new ArrayList(0);

        vnList.addAll(goldService.getGoldPriceWithRange("VND", fromDate, toDate));
        inList.addAll(goldService.getGoldPriceWithRange("USD", fromDate, toDate));
        List<GoldPrice> vnListAdded = new ArrayList();
        List<GoldPrice> inListAdded = new ArrayList();

        LOGGER.info("VN list =" + vnList.size());
        LOGGER.info("IN list =" + inList.size());

        List<Long> timeList = new ArrayList<Long>();
        for (GoldPrice p : vnList) {
            GoldPrice unit = new GoldPrice();
            unit.setTime(p.getTime());
            unit.setCurrency("USD");
            inListAdded.add(unit);
            timeList.add(p.getTime());
        }

        for (GoldPrice p : inList) {
            if (!timeList.contains(p.getTime())) {
                GoldPrice unit = new GoldPrice();
                unit.setTime(p.getTime());
                unit.setCurrency("VND");
                vnListAdded.add(unit);
                timeList.add(p.getTime());
            }
        }

        vnList.addAll(vnListAdded);
        // perform sorting
        java.util.Collections.sort(vnList, new GoldPriceSortByTime());

        inList.addAll(inListAdded);
        java.util.Collections.sort(inList, new GoldPriceSortByTime());

        for (int i = 0; i < vnList.size(); i++) {
            GoldPrice p = vnList.get(i);
            if (p.getPriceBuy() == null) {
                if (i != 0) {
                    GoldPrice previous = vnList.get(i - 1);
                    if (previous.getPriceBuy() != null) {
                        p.setPriceBuy(previous.getPriceBuy());
                        p.setPriceSell(previous.getPriceSell());
                    }
                }

            }

            GoldPrice g = inList.get(i);
            if (g.getPriceBuy() == null) {
                if (i != 0) {
                    GoldPrice previous = inList.get(i - 1);
                    if (previous.getPriceBuy() != null) {
                        g.setPriceBuy(previous.getPriceBuy());
                        g.setPriceSell(previous.getPriceSell());
                    }
                }

            }
        }

        LOGGER.info(vnList.toArray().toString());


        result.put("VND", vnList);
        result.put("USD", inList);

        return result;
    }

    public GoldManager getGoldService() {
        return goldService;
    }

    public void setGoldService(GoldManager goldService) {
        this.goldService = goldService;
    }
}
