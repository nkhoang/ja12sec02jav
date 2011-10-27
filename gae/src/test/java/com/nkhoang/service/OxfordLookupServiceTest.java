package com.nkhoang.service;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
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
        Word w = oxfordLookupService.lookup("display");
        w.getDescription();
    }
}
