package com.nkhoang.gae.action;

import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.GoldManager;
import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.service.CurrencyService;
import com.nkhoang.gae.utils.DateConverter;
import com.nkhoang.gae.utils.WebUtils;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/currency")
public class CurrencyAction {
	private static final Logger LOG = LoggerFactory.getLogger(CurrencyAction.class);
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

	@RequestMapping(value = "/updateExchangeRate", method = RequestMethod.POST)
	public ModelAndView updateExchangeRate(
		@RequestParam String startDate, @RequestParam String endDate, @RequestParam String viewState) {
		ModelAndView mav = new ModelAndView();
		mav.setView(new JSONView());

		List<String> errorMessages = new ArrayList<String>();

		// convert date to Date object
		try {
			long dayDiff = 0;

			Date startDateObj = DateUtils.parseDate(startDate, new String[] {"dd/MM/yyyy"});
			Date endDateObj = DateUtils.parseDate(endDate, new String[] {"dd/MM/yyyy"});

			if (endDateObj.before(startDateObj)) {
				errorMessages.add("end date is before start date. Please check again.");
			} else {
				Calendar cal = Calendar.getInstance();
				// get the year of start date.
				cal.setTime(startDateObj);
				int startYear = cal.get(Calendar.YEAR);
				long startDateFragment = cal.get(Calendar.DAY_OF_YEAR);

				cal.setTime(endDateObj);
				int endYear = cal.get(Calendar.YEAR);
				long endDateFragment = cal.get(Calendar.DAY_OF_YEAR);

				if (endYear == startYear) {
					dayDiff = endDateFragment - startDateFragment;
				} else {
					dayDiff = (endYear - startYear - 1) * 365 + (365 - startDateFragment) + endDateFragment;
				}
				LOG.info(String.format("Day diff of [%s] and [%s] is : %s", startDate, endDate, dayDiff));
				for (int i = 0 ;i < dayDiff; i++) {
					Date newDate = DateUtils.addDays(startDateObj, i);
					List<Currency> currencies = currencyService.getCurrencyDateFromVCB(newDate, viewState);
				}
			}
		}
		catch (ParseException pe) {
			// could not parse date to Date object
			errorMessages.add("Could not parse provided date. Please check again.");
			mav.addObject(GSONStrategy.DATA, errorMessages);
		}
		return mav;
	}
}
