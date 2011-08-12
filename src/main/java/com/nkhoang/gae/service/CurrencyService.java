package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Currency;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface CurrencyService {
	List<Currency> getCurrencyDateFromVCB(Date date, String viewState);

	Date getVCBDate(String date) throws ParseException;

	String getVCBDateFormat(Date date);
}
