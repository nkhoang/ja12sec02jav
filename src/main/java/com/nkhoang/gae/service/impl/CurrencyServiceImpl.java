package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.CurrencyDao;
import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.service.CurrencyService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CurrencyServiceImpl implements CurrencyService {
	private static final Logger LOG = LoggerFactory.getLogger(CurrencyServiceImpl.class.getCanonicalName());
	public static final String VCB_DATE_FORMAT = "dd/MM/yyyy";
	private String vcbUri;

	public String getVcbUri() {
		return vcbUri;
	}

	public void setVcbUri(String vcbUri) {
		this.vcbUri = vcbUri;
	}


	public String getVCBDateFormat(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(VCB_DATE_FORMAT);
		return dateFormat.format(date);
	}

	public Date getVCBDate(String date) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(VCB_DATE_FORMAT);
		return dateFormat.parse(date);
	}

	/**
	 * This method will send a POST request to VCB URL and then parse the received data.
	 *
	 * @param date      date to get the exchange rate.
	 * @param viewState liek /wEPDwUKMTQxNzA1MDE2Mg9kFgJmD2QWAgIDD2QWBAIBD2QWAgIDD2QWAgICDxYCHgtfIUl0ZW1Db3VudAIIFhACAQ9kFgICAQ8PFgQeBFRleHQFC1RyYW5nIGNo4bunHgtOYXZpZ2F0ZVVybAUCfi9kZAICD2QWAgIBDw8WBB8BBQlDw6EgbmjDom4fAgULfi9QZXJzb25hbC9kZAIDD2QWAgIBDw8WBB8BBQ5Eb2FuaCBuZ2hp4buHcB8CBQ1+L0NvcnBvcmF0ZXMvZGQCBA9kFgICAQ8PFgQfAQUZxJDhu4tuaCBjaOG6vyB0w6BpIGNow61uaB8CBQV+L0ZJL2RkAgUPZBYCAgEPDxYEHwEFGE5nw6JuIGjDoG5nIMSRaeG7h24gdOG7rR8CBQt+L0VCYW5raW5nL2RkAgYPZBYCAgEPDxYEHwEFDlR1eeG7g24gZOG7pW5nHwIFCn4vQ2FyZWVycy9kZAIHD2QWAgIBDw8WBB8BBQ9OaMOgIMSR4bqndSB0xrAfAgUMfi9JbnZlc3RvcnMvZGQCCA9kFgICAQ8PFgQfAQUOR2nhu5tpIHRoaeG7h3UfAgUIfi9BYm91dC9kZAILD2QWAgIBD2QWAgIBDxYCHwACBRYKAgEPZBYCAgEPDxYEHwEFG8SQaeG7gXUga2hv4bqjbiBz4butIGThu6VuZx8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAICD2QWAgIBDw8WBB8BBQtC4bqjbyBt4bqtdB8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAIDD2QWAgIBDw8WBB8BBQpMacOqbiBo4buHHwIFJm1haWx0bzp3ZWJtYXN0ZXJbYXRddmlldGNvbWJhbmsuY29tLnZuZGQCBA9kFgICAQ8PFgQfAQURU8ahIMSR4buTIHdlYnNpdGUfAgUOfi9TaXRlbWFwLmFzcHhkZAIFD2QWAgIBDw8WBB8BBRRWaWV0Y29tYmFuayBXZWIgTWFpbB8CBSxodHRwOi8vZXhjaGFuZ2UudmlldGNvbWJhbmsuY29tLnZuL2V4Y2hhbmdlL2RkGAIFGGN0bDAwJENvbnRlbnQkRXhyYXRlVmlldw88KwAKAQgCAWQFKmN0bDAwJEhlYWRlciRMYW5ndWFnZVN3aXRjaGVyJExhbmd1YWdlVmlldw8PZAIBZMyR3fzbIXU1Ttj1MtjvWSlSA6YD
	 *
	 * @return
	 */
	public List<Currency> getCurrencyDateFromVCB(Date date, String viewState) {
		List<Currency> c = null;
		HttpClient client = new HttpClient();
		String targetDate = getVCBDateFormat(date);
		LOG.info(targetDate);
		PostMethod post = new PostMethod("http://www.vietcombank.com.vn/ExchangeRates/Default.aspx");
		NameValuePair[] parameters = {new NameValuePair("__VIEWSTATE", viewState), new NameValuePair(
			"ctl00$Content$BranchList", "68"), new NameValuePair(
			"ctl00$Content$DateText", targetDate), new NameValuePair(
			"ctl00$Content$ViewButton", "Xem")};

		post.setRequestBody(parameters);

		post.setRequestHeader("Host", "www.vietcombank.com.vn");
		post.setRequestHeader(
			"User-Agent",
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-US) AppleWebKit/534.10 (KHTML, like Gecko) Chrome/8.0.552.231 Safari/534.10");
		post.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setRequestHeader("Referer", "http://www.vietcombank.com.vn/ExchangeRates/");

		try {
			client.executeMethod(post);
			c = parseHttpResponse(post.getResponseBodyAsStream(), date);

		}
		catch (Exception e) {
			LOG.error(String.format("Could not communicate with service URL : %s", vcbUri), e);
		}

		return c;
	}

	/**
	 * Convert float number in String to Float.
	 *
	 * @param number number in format:   7,003.92
	 *
	 * @return Float number.
	 */
	private Float convertStringToFloat(String number) {
		Float f = 0f;
		try {
			number = number.replace(",", "");
			f = Float.parseFloat(number);
		}
		catch (NumberFormatException nfex) {
			// LOG.error(String.format("Could not parse number : [%s] to float.", number));
		}
		return f;
	}

	private List<Currency> parseHttpResponse(InputStream is, Date date) {
		List<Currency> currencyList = new ArrayList<Currency>();
		try {
			Source source = new Source(is);
			List<Element> priceTbls = source.getAllElementsByClass("rateTable");
			if (CollectionUtils.isNotEmpty(priceTbls)) {
				// by default there is only one price table.
				Element priceTbl = priceTbls.get(0);
				List<Element> priceTRs = priceTbl.getAllElementsByClass("odd");
				if (CollectionUtils.isNotEmpty(priceTRs)) {
					for (Element priceTR : priceTRs) {
						List<Element> priceTDs = priceTR.getAllElements("td");
						if (CollectionUtils.isNotEmpty(priceTDs) && priceTDs.size() >= 5) {
							Currency currency = new Currency();
							currency.setCurrency(priceTDs.get(0).getContent().toString());
							currency.setPriceBuy(convertStringToFloat(priceTDs.get(4).getContent().toString()));
							currency.setPriceSell(convertStringToFloat(priceTDs.get(2).getContent().toString()));
							currency.setTime(date.getTime());

							currencyList.add(currency);
						}
					}
				}
			}
		}
		catch (Exception e) {
			LOG.error("Could not parse Currency HTML from response input stream.", e);
		}

		return currencyList;
	}
}
