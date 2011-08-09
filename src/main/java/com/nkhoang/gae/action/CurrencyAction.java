package com.nkhoang.gae.action;

import com.nkhoang.gae.manager.GoldManager;
import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.service.CurrencyService;
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
@RequestMapping("/currency")
public class CurrencyAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyAction.class);
    @Autowired
    private CurrencyService currencyService;

    public CurrencyService getCurrencyService() {
        return currencyService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @RequestMapping("/exchangeRate")
    public String renderExchangeRateHomePage() {
        return "currency/index";
    }
}
