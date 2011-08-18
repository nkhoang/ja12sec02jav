package com.nkhoang.common.lucene;

import static org.junit.Assert.assertEquals;

import com.nkhoang.common.lucene.LuceneUtils;
import com.nkhoang.lucene.LuceneSearchUtil;
import org.apache.poi.util.SystemOutLogger;
import org.junit.Test;

public class LuceneUtilsTest {

  @Test
  public void testEscape() {
	  System.out.println(LuceneSearchUtil.escapeSpecialChar("( +state~~city~~addr1:wy~~cheyenne~~1950 bluegrass cir )"));
	  System.out.println(LuceneUtils.escapeSearchText("( +state~~city~~addr1:wy~~cheyenne~~1950 bluegrass cir )"));
  }

  @Test
  public void whitespace() {
    assertEquals("Di\\ Prinzio", LuceneUtils.escapeSearchText("Di Prinzio"));
  }


  @Test
  public void whitespaces() {
    assertEquals("\\ Di\\ \\ Prinzio\\ ", LuceneUtils
            .escapeSearchText(" Di  Prinzio "));
  }


  @Test
  public void minus() {
    assertEquals("Joliot\\-Curie", LuceneUtils.escapeSearchText("Joliot-Curie"));
  }


  @Test
  public void plus() {
    assertEquals("a\\+b", LuceneUtils.escapeSearchText("a+b"));
  }


  @Test
  public void backslash() {
    assertEquals("\\\\n", LuceneUtils.escapeSearchText("\\n"));
  }


  @Test
  public void singleQuote() {
    assertEquals("O'Malley", LuceneUtils.escapeSearchText("O'Malley"));
  }


  @Test
  public void colon() {
    assertEquals("property\\:value", LuceneUtils
            .escapeSearchText("property:value"));
  }


  @Test
  public void andescape() {
    assertEquals("\\and", LuceneUtils.escapeSearchText("and"));
  }


  @Test
  public void orescape() {
    assertEquals("\\or", LuceneUtils.escapeSearchText("or"));
  }


  @Test
  public void notescape() {
    assertEquals("\\not", LuceneUtils.escapeSearchText("not"));
  }


  @Test
  public void reserverwordInMiddle() {
    assertEquals("computer\\ and\\ science", LuceneUtils
            .escapeSearchText("computer and science"));
  }

  @Test
  public void excludechar() {
    assertEquals("exclu*e\\&", LuceneUtils.escapeSearchText("exclu*e&","*"));
  }

  @Test
  public void excludechars() {
    assertEquals("ex\\\\clu*e&", LuceneUtils.escapeSearchText("ex\\clu*e&",
            new String[] {
              "*","&"
            }));
  }

}
