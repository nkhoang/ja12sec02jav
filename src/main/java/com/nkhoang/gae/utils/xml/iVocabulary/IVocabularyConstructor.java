package com.nkhoang.gae.utils.xml.iVocabulary;

import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Word;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class IVocabularyConstructor {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(IVocabularyConstructor.class.getCanonicalName());
  private static final String IVOCABULARY_DATE_TIME_FORMAT = "dd/MM/yyyy";
  private static final String IVOCABULARY_TIMEZONE = "Asia/Bangkok";

  /**
   * Contruct iVocabulary file from database.
   *
   * @param startingIndex word offset.
   * @param size          iVocabulary page size.
   * @return xml string.
   */
  public String constructIVocabularyFile(
      List<Word> allWords, int startingIndex, int size, int pageSize, String dateTime, String chapterTitle,
      List<Long> meaningIds, Map<String, List<Integer>> exampleIds) {
    String xml = constructXMLBlockContent(
        allWords, pageSize, startingIndex, size, dateTime, chapterTitle, meaningIds, exampleIds);

    String xmlStr = "";
    try {
      InputStream is = this.getClass().getResourceAsStream("/vocabulary.xml");

      if (is == null) {
        LOGGER.error("Could not load resources.");
      }

      // Instantiate VTDGen
      VTDGen vg = new VTDGen();
      StringWriter writer = new StringWriter();
      IOUtils.copy(is, writer);
      String theString = writer.toString();
      vg.setDoc(theString.getBytes());

      vg.parse(true);

      //Instantiate XMLModifier
      XMLModifier xm = new XMLModifier();
      LOGGER.info("Starting to parse XML");
      VTDNav vn = vg.getNav();

      xm.bind(vn);

      AutoPilot ap = new AutoPilot(vn);

      ap.selectXPath("/Vocabulary/Root");
      int i = -1;
      while ((i = ap.evalXPath()) != -1) {
        xm.insertAfterHead(xml);
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      xm.output(bos);

      xmlStr = bos.toString("UTF-8");


    } catch (Exception e) {
      LOGGER.info("Could not parse or update vocabulary.xml file.");
    }
    return xmlStr;
  }

  /**
   * Contruct XML complied with iVocabulary XVOC format.
   *
   * @param allWords      list of words.
   * @param size          number of words contained in a Page.
   * @param startingIndex offset of the list.
   * @param requestSize   number of words will be processed.
   * @return xml string.
   */

  private static String constructXMLBlockContent(
      List<Word> allWords, int size, int startingIndex, int requestSize, String dateTime, String chapterTitle,
      List<Long> meaningIds, Map<String, List<Integer>> exampleIds) {
    SimpleDateFormat formatter = new SimpleDateFormat(IVOCABULARY_DATE_TIME_FORMAT, Locale.US);
    formatter.setTimeZone(TimeZone.getTimeZone(IVOCABULARY_TIMEZONE));

    Date currentDate = GregorianCalendar.getInstance().getTime();
    try {
      // dateTime = "20/11/2010"
      currentDate = formatter.parse(dateTime);
      int incrementDay = 0;
      if (startingIndex != 0) {
        incrementDay = (startingIndex + requestSize) / size;
      } else if (startingIndex == 0) {
        incrementDay = requestSize / size;
      }

      currentDate = DateUtils.addDays(currentDate, incrementDay + 1);
    } catch (Exception e) {
      LOGGER.info("Exception occured when calculating datetime => Use current date.");

    }

    StringBuilder xmlBuilder = new StringBuilder();

    // first build <Chapter> tag.
    xmlBuilder.append("<Chapter title='" + chapterTitle + "' >");
    int counter = 1;
    int dayCounter = -1;
    for (Word w : allWords) {
      if (counter == 1 || counter > size) {
        counter = 1; // reset counter.
        String dateStr = formatter.format(DateUtils.addDays(currentDate, dayCounter - 1));
        dayCounter++;
        // build <Page> tag.
        xmlBuilder.append("<Page title='" + chapterTitle + " - " + dateStr + "' >");
      }

      StringBuilder comment = new StringBuilder();
      StringBuilder targetWords = new StringBuilder();
      // set Pron
      String pron = w.getPron() == null ? "" : w.getPron();

      List<Meaning> lmn = w.getMeaning(w.getKindidmap().get("noun")); // meaning for noun.
      if (lmn != null && lmn.size() > 0) {
        for (Meaning m : lmn) {
          if (meaningIds.contains(m.getId())) {
            String content = m.getContent();

            targetWords.append("(n) " + content + "\n");
            if (m.getExamples() != null && m.getExamples().size() > 0 && exampleIds.get(m.getId() + "") != null) {
              for (Integer i : exampleIds.get(m.getId() + "")) {
                comment.append(" (n) " + m.getExamples().get(i) + "\n");
              }
            }
          }
        }
      }
      List<Meaning> lmv = w.getMeaning(w.getKindidmap().get("verb")); // meaning for verb.
      if (lmv != null && lmv.size() > 0) {
        for (Meaning m : lmv) {
          if (meaningIds.contains(m.getId())) {
            String content = m.getContent();

            targetWords.append("(v) " + content + "\n");
            if (m.getExamples() != null && m.getExamples().size() > 0 && exampleIds.get(m.getId() + "") != null) {
              for (Integer i : exampleIds.get(m.getId() + "")) {
                comment.append(" (v) " + m.getExamples().get(i) + "\n");
              }
            }
          }
        }
      }

      List<Meaning> lmadj = w.getMeaning(w.getKindidmap().get("adjective")); // meaning for adjective
      if (lmadj != null && lmadj.size() > 0) {
        for (Meaning m : lmadj) {
          if (meaningIds.contains(m.getId())) {

            String content = m.getContent();

            targetWords.append("(adj) " + content + "\n");
            if (m.getExamples() != null && m.getExamples().size() > 0 && exampleIds.get(m.getId() + "") != null) {
              for (Integer i : exampleIds.get(m.getId() + "")) {
                comment.append(" (adj) " + m.getExamples().get(i) + "\n");
              }
            }
          }
        }
      }

      List<Meaning> lmadv = w.getMeaning(w.getKindidmap().get("adverb")); // meaning for adv.
      if (lmadv != null && lmadv.size() > 0) {
        for (Meaning m : lmadj) {
          if (meaningIds.contains(m.getId())) {

            String content = m.getContent();

            targetWords.append("(adv) " + content + "\n");
            if (m.getExamples() != null && m.getExamples().size() > 0 && exampleIds.get(m.getId() + "") != null) {
              for (Integer i : exampleIds.get(m.getId() + "")) {
                comment.append(" (adv) " + m.getExamples().get(i) + "\n");
              }
            }
          }
        }
      }

      if (StringUtils.isNotBlank(targetWords.toString().trim())) {
        xmlBuilder.append(
            "<Word sourceWord=\"" + w.getDescription() + "\" targetWord=\"" +
                targetWords.toString() + "\">");
        if (StringUtils.isNotEmpty(comment.toString())) {
          xmlBuilder.append("<Comment>" + pron + "\n" + comment.toString() + "</Comment>");
        }
        xmlBuilder.append("</Word>");
      }

      if (counter + (size * dayCounter) == allWords.size()) {
        xmlBuilder.append("</Page>"); // append ending tag.
      } else {

        counter++;
        if (counter > size) {
          xmlBuilder.append("</Page>"); // append ending tag.
        }
      }
    }

    xmlBuilder.append("</Chapter>");

    return xmlBuilder.toString();
  }

}
