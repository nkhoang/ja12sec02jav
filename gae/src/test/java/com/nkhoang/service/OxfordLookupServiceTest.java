package com.nkhoang.service;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class OxfordLookupServiceTest {
    @Autowired
    private LookupService oxfordLookupService;
    @Test
    public void testLookup() {
        Word w = oxfordLookupService.lookup("come");
        w.getDescription();
    }

  @Test
  public void testPhone(){
    String s1 = "0123456789";
    String s2 = "0123456789";
    System.out.println(compareAddressPhoneNumber(s2, s1));
  }

  private boolean compareAddressPhoneNumber(String practPhone, String addressPhone) {
    if (practPhone.length() == 7) {
      if (addressPhone.length() > 7) {
        return StringUtils.equals(
            addressPhone.substring(addressPhone.length() - 7), practPhone);
      }
    }
    else if (practPhone.length() == 10) {
      return StringUtils.equals(addressPhone, practPhone);
    }
    return false;
  }
}
