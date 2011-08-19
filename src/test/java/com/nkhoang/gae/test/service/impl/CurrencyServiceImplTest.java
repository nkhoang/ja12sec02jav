package com.nkhoang.gae.test.service.impl;


import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.service.CurrencyService;
import com.nkhoang.gae.service.impl.CurrencyServiceImpl;
import com.nkhoang.gae.utils.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml"})
public class CurrencyServiceImplTest {
	private static final Logger LOG = LoggerFactory.getLogger(CurrencyServiceImplTest.class);

	@Autowired
	private CurrencyService currencyService;

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@Test
	public void testGetCurrencyDateFromVCB() {
		Date date = Calendar.getInstance().getTime();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(CurrencyServiceImpl.VCB_DATE_FORMAT);
			date = dateFormat.parse("09/08/2010");
		}
		catch (ParseException pex) {
			// do nothing.     e
		}

		System.out.println(date.toString());
		List<Currency> currencyList = currencyService.getCurrencyDateFromVCB(
			date,
			"/wEPDwUKMTQxNzA1MDE2Mg9kFgJmD2QWAgIDD2QWBAIBD2QWAgIDD2QWAgICDxYCHgtfIUl0ZW1Db3VudAIIFhACAQ9kFgICAQ8PFgQeBFRleHQFC1RyYW5nIGNo4bunHgtOYXZpZ2F0ZVVybAUCfi9kZAICD2QWAgIBDw8WBB8BBQlDw6EgbmjDom4fAgULfi9QZXJzb25hbC9kZAIDD2QWAgIBDw8WBB8BBQ5Eb2FuaCBuZ2hp4buHcB8CBQ1+L0NvcnBvcmF0ZXMvZGQCBA9kFgICAQ8PFgQfAQUZxJDhu4tuaCBjaOG6vyB0w6BpIGNow61uaB8CBQV+L0ZJL2RkAgUPZBYCAgEPDxYEHwEFGE5nw6JuIGjDoG5nIMSRaeG7h24gdOG7rR8CBQt+L0VCYW5raW5nL2RkAgYPZBYCAgEPDxYEHwEFDlR1eeG7g24gZOG7pW5nHwIFCn4vQ2FyZWVycy9kZAIHD2QWAgIBDw8WBB8BBQ9OaMOgIMSR4bqndSB0xrAfAgUMfi9JbnZlc3RvcnMvZGQCCA9kFgICAQ8PFgQfAQUOR2nhu5tpIHRoaeG7h3UfAgUIfi9BYm91dC9kZAILD2QWAgIBD2QWAgIBDxYCHwACBRYKAgEPZBYCAgEPDxYEHwEFG8SQaeG7gXUga2hv4bqjbiBz4butIGThu6VuZx8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAICD2QWAgIBDw8WBB8BBQtC4bqjbyBt4bqtdB8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAIDD2QWAgIBDw8WBB8BBQpMacOqbiBo4buHHwIFJm1haWx0bzp3ZWJtYXN0ZXJbYXRddmlldGNvbWJhbmsuY29tLnZuZGQCBA9kFgICAQ8PFgQfAQURU8ahIMSR4buTIHdlYnNpdGUfAgUOfi9TaXRlbWFwLmFzcHhkZAIFD2QWAgIBDw8WBB8BBRRWaWV0Y29tYmFuayBXZWIgTWFpbB8CBSxodHRwOi8vZXhjaGFuZ2UudmlldGNvbWJhbmsuY29tLnZuL2V4Y2hhbmdlL2RkGAIFGGN0bDAwJENvbnRlbnQkRXhyYXRlVmlldw88KwAKAQgCAWQFKmN0bDAwJEhlYWRlciRMYW5ndWFnZVN3aXRjaGVyJExhbmd1YWdlVmlldw8PZAIBZMyR3fzbIXU1Ttj1MtjvWSlSA6YD");

		for (Currency c : currencyList)
			System.out.println(c);
	}

	@Test
	public void testWriteToCSV() {
		// convert date to Date object
		try {
			long dayDiff = 0;

			String startDate = "01/07/2011";
			String endDate = "01/08/2011";

			Date startDateObj = DateUtils.parseDate(startDate, new String[]{"dd/MM/yyyy"});
			Date endDateObj = DateUtils.parseDate(endDate, new String[]{"dd/MM/yyyy"});

			if (endDateObj.before(startDateObj)) {
				LOG.info("End date is before start date. Please check again.");
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

				Map<String, List<Currency>> currencyMap = new HashMap<String, List<Currency>>();

				for (int i = 0; i < dayDiff; i++) {
					Date newDate = DateUtils.addDays(startDateObj, i);
					List<Currency> currencies = currencyService.getCurrencyDateFromVCB(
						newDate,
						"/wEPDwUKMTQxNzA1MDE2Mg9kFgJmD2QWAgIDD2QWBAIBD2QWAgIDD2QWAgICDxYCHgtfIUl0ZW1Db3VudAIIFhACAQ9kFgICAQ8PFgQeBFRleHQFC1RyYW5nIGNo4bunHgtOYXZpZ2F0ZVVybAUCfi9kZAICD2QWAgIBDw8WBB8BBQlDw6EgbmjDom4fAgULfi9QZXJzb25hbC9kZAIDD2QWAgIBDw8WBB8BBQ5Eb2FuaCBuZ2hp4buHcB8CBQ1+L0NvcnBvcmF0ZXMvZGQCBA9kFgICAQ8PFgQfAQUZxJDhu4tuaCBjaOG6vyB0w6BpIGNow61uaB8CBQV+L0ZJL2RkAgUPZBYCAgEPDxYEHwEFGE5nw6JuIGjDoG5nIMSRaeG7h24gdOG7rR8CBQt+L0VCYW5raW5nL2RkAgYPZBYCAgEPDxYEHwEFDlR1eeG7g24gZOG7pW5nHwIFCn4vQ2FyZWVycy9kZAIHD2QWAgIBDw8WBB8BBQ9OaMOgIMSR4bqndSB0xrAfAgUMfi9JbnZlc3RvcnMvZGQCCA9kFgICAQ8PFgQfAQUOR2nhu5tpIHRoaeG7h3UfAgUIfi9BYm91dC9kZAILD2QWAgIBD2QWAgIBDxYCHwACBRYKAgEPZBYCAgEPDxYEHwEFG8SQaeG7gXUga2hv4bqjbiBz4butIGThu6VuZx8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAICD2QWAgIBDw8WBB8BBQtC4bqjbyBt4bqtdB8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAIDD2QWAgIBDw8WBB8BBQpMacOqbiBo4buHHwIFJm1haWx0bzp3ZWJtYXN0ZXJbYXRddmlldGNvbWJhbmsuY29tLnZuZGQCBA9kFgICAQ8PFgQfAQURU8ahIMSR4buTIHdlYnNpdGUfAgUOfi9TaXRlbWFwLmFzcHhkZAIFD2QWAgIBDw8WBB8BBRRWaWV0Y29tYmFuayBXZWIgTWFpbB8CBSxodHRwOi8vZXhjaGFuZ2UudmlldGNvbWJhbmsuY29tLnZuL2V4Y2hhbmdlL2RkGAIFGGN0bDAwJENvbnRlbnQkRXhyYXRlVmlldw88KwAKAQgCAWQFKmN0bDAwJEhlYWRlciRMYW5ndWFnZVN3aXRjaGVyJExhbmd1YWdlVmlldw8PZAIBZMyR3fzbIXU1Ttj1MtjvWSlSA6YD");
					for (Currency c : currencies) {
						if (currencyMap.get(c.getCurrency()) == null) {
							List<Currency> specificCurrency = new ArrayList<Currency>();
							specificCurrency.add(c);

							currencyMap.put(c.getCurrency(), specificCurrency);
						} else {
							currencyMap.get(c.getCurrency()).add(c);
						}
					}
				}
				for (String currencyName : currencyMap.keySet()) {
					String fileName = "D:/" + currencyName + ".csv";
					List<String> rowData = new ArrayList<String>(0);
					for (Currency c : currencyMap.get(currencyName)) {
						StringBuilder row = new StringBuilder();
						row.append(c.getCurrency());
						row.append(",");
						row.append(c.getPriceBuy() != null ? c.getPriceBuy() : "");
						row.append(",");
						row.append(c.getPriceSell() != null ? c.getPriceSell() : "");
						row.append(",");
						cal = Calendar.getInstance();
						cal.setTimeInMillis(c.getTime());
						SimpleDateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
						row.append(formater.format(cal.getTime()));

						rowData.add(row.toString());
					}
					try {
						FileUtils.writeToCSV(rowData, fileName);
					}
					catch (IOException ioe) {
						LOG.error("COuld not locate target file.", ioe);
					}
				}
			}
		}
		catch (ParseException pe) {
			// could not parse date to Date object
			LOG.info("Could not parse provided date. Please check again.");
		}
	}
}
