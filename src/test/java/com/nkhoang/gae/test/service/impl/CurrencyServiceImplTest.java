package com.nkhoang.gae.test.service.impl;


import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.service.impl.CurrencyServiceImpl;
import org.apache.commons.lang.time.DateUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml"})
public class CurrencyServiceImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyServiceImplTest.class);

    @Test
    public void testGetCurrencyDateFromVCB() {
        CurrencyServiceImpl service = new CurrencyServiceImpl();
            Date date = Calendar.getInstance().getTime();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(CurrencyServiceImpl.VCB_DATE_FORMAT);
            date = dateFormat.parse("09/08/2010");
        } catch (ParseException pex) {
            // do nothing.
        }

        System.out.println(date.toString());
        List<Currency> currencyList = service.getCurrencyDateFromVCB(date, "/wEPDwUKMTQxNzA1MDE2Mg9kFgJmD2QWAgIDD2QWBAIBD2QWAgIDD2QWAgICDxYCHgtfIUl0ZW1Db3VudAIIFhACAQ9kFgICAQ8PFgQeBFRleHQFC1RyYW5nIGNo4bunHgtOYXZpZ2F0ZVVybAUCfi9kZAICD2QWAgIBDw8WBB8BBQlDw6EgbmjDom4fAgULfi9QZXJzb25hbC9kZAIDD2QWAgIBDw8WBB8BBQ5Eb2FuaCBuZ2hp4buHcB8CBQ1+L0NvcnBvcmF0ZXMvZGQCBA9kFgICAQ8PFgQfAQUZxJDhu4tuaCBjaOG6vyB0w6BpIGNow61uaB8CBQV+L0ZJL2RkAgUPZBYCAgEPDxYEHwEFGE5nw6JuIGjDoG5nIMSRaeG7h24gdOG7rR8CBQt+L0VCYW5raW5nL2RkAgYPZBYCAgEPDxYEHwEFDlR1eeG7g24gZOG7pW5nHwIFCn4vQ2FyZWVycy9kZAIHD2QWAgIBDw8WBB8BBQ9OaMOgIMSR4bqndSB0xrAfAgUMfi9JbnZlc3RvcnMvZGQCCA9kFgICAQ8PFgQfAQUOR2nhu5tpIHRoaeG7h3UfAgUIfi9BYm91dC9kZAILD2QWAgIBD2QWAgIBDxYCHwACBRYKAgEPZBYCAgEPDxYEHwEFG8SQaeG7gXUga2hv4bqjbiBz4butIGThu6VuZx8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAICD2QWAgIBDw8WBB8BBQtC4bqjbyBt4bqtdB8CBRh+L1VuZGVyQ29uc3RydWN0aW9uLmFzcHhkZAIDD2QWAgIBDw8WBB8BBQpMacOqbiBo4buHHwIFJm1haWx0bzp3ZWJtYXN0ZXJbYXRddmlldGNvbWJhbmsuY29tLnZuZGQCBA9kFgICAQ8PFgQfAQURU8ahIMSR4buTIHdlYnNpdGUfAgUOfi9TaXRlbWFwLmFzcHhkZAIFD2QWAgIBDw8WBB8BBRRWaWV0Y29tYmFuayBXZWIgTWFpbB8CBSxodHRwOi8vZXhjaGFuZ2UudmlldGNvbWJhbmsuY29tLnZuL2V4Y2hhbmdlL2RkGAIFGGN0bDAwJENvbnRlbnQkRXhyYXRlVmlldw88KwAKAQgCAWQFKmN0bDAwJEhlYWRlciRMYW5ndWFnZVN3aXRjaGVyJExhbmd1YWdlVmlldw8PZAIBZMyR3fzbIXU1Ttj1MtjvWSlSA6YD");

        for (Currency c : currencyList)
            System.out.println(c);
    }
}
