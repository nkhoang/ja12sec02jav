package com.nkhoang.gae.action;

import com.nkhoang.gae.view.constant.ViewConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/chart")
public class ViewChartAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewChartAction.class);
    @RequestMapping("/" + ViewConstant.VIEW_CHART_REQUEST)
    public ModelAndView update(HttpServletResponse response) {

        return new ModelAndView("chartView");
    }

}
