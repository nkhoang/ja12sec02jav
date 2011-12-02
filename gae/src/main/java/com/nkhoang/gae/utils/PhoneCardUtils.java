package com.nkhoang.gae.utils;

import com.google.gdata.util.ServiceException;
import com.nkhoang.gae.exception.GAEException;
import com.nkhoang.gae.model.PhoneCardDiscount;
import com.nkhoang.gae.service.SpreadsheetService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneCardUtils {
  private static final Logger LOG = LoggerFactory.getLogger(PhoneCardUtils.class.getCanonicalName());
  private static final String PHONECARD_DISCOUNT_SPREADSHEET_NAME = "chietkhau";
  private static final String PHONECARD_DISCOUNT_SHEETNAME = "chietkhau";
  private static final int PHONECARD_FETCHING_ROW = 3;
  private static final int PHONECARD_FETCHING_SIZE = 23;
  private static final int PHONECARD_UPDATE_DATE_COL = 1;
  private static final int PHONECARD_UPDATE_DATE_ROW = 2;
  private static final int PHONECARD_TYPE_COL = 1;
  private static final int PHONECARD_PRICE = 2;
  private static final int PHONECARD_DISCOUNT_SELLER = 3;
  private static final int PHONECARD_DISCOUNT_1_MIL_COL = 5;
  private static final int PHONECARD_DISCOUNT_5_MIL_COL = 7;
  private static final int PHONECARD_DISCOUNT_10_MIL_COL = 9;
  private static final int PHONECARD_DISCOUNT_20_MIL_COL = 11;
  public static final String PHONECARD_DISCOUNT_TYPE_1 = "1";
  public static final String PHONECARD_DISCOUNT_TYPE_5 = "5";
  public static final String PHONECARD_DISCOUNT_TYPE_10 = "10";
  public static final String PHONECARD_DISCOUNT_TYPE_20 = "20";
  private static final String PHONECARD_DATE_PATTERN = "MM/dd/yyyy";

  /**
   * Get latest PhoneCard update from Google Docs.
   *
   * @param spreadsheetService the spreadsheet service.
   * @return the latest phonecard discount list.
   * @throws GAEException if there is any exception.
   */
  public static List<PhoneCardDiscount> getLatestPhoneCard(SpreadsheetService spreadsheetService) throws GAEException {
    List<PhoneCardDiscount> phoneCardDiscounts = new ArrayList<PhoneCardDiscount>();
    try {
      List<String> dates = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_UPDATE_DATE_ROW, PHONECARD_UPDATE_DATE_COL, 1);
      List<String> phonecardType = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_FETCHING_ROW, PHONECARD_TYPE_COL, PHONECARD_FETCHING_SIZE);
      List<String> discountRateSeller = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_FETCHING_ROW, PHONECARD_DISCOUNT_SELLER, PHONECARD_FETCHING_SIZE);
      List<String> discountRate_1 = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_FETCHING_ROW, PHONECARD_DISCOUNT_1_MIL_COL, PHONECARD_FETCHING_SIZE);
      List<String> discountRate_5 = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_FETCHING_ROW, PHONECARD_DISCOUNT_5_MIL_COL, PHONECARD_FETCHING_SIZE);
      List<String> discountRate_10 = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_FETCHING_ROW, PHONECARD_DISCOUNT_10_MIL_COL, PHONECARD_FETCHING_SIZE);
      List<String> discountRate_20 = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_FETCHING_ROW, PHONECARD_DISCOUNT_20_MIL_COL, PHONECARD_FETCHING_SIZE);
      List<String> phonecardPrice = spreadsheetService.querySpreadsheet(
          PHONECARD_DISCOUNT_SPREADSHEET_NAME, PHONECARD_DISCOUNT_SHEETNAME, PHONECARD_FETCHING_ROW, PHONECARD_PRICE, PHONECARD_FETCHING_SIZE);

      if (CollectionUtils.isEmpty(dates)) {
        throw new GAEException("Could not get current date.", 1);
      }
      DateTime fetchTime = null;
      try {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(PHONECARD_DATE_PATTERN);
        fetchTime = fmt.parseDateTime(dates.get(0));
      } catch (IllegalArgumentException ilEx) {
        // TODO: update error code here.
        throw new GAEException("Could not parse current date.", 1, ilEx);
      }

      // build phonecard object.
      for (int i = 0; i < phonecardType.size(); i++) {
        PhoneCardDiscount discount = new PhoneCardDiscount();
        // add time.
        discount.setDate(fetchTime);
        discount.setPrice(convertPhoneCardPrice(phonecardPrice.get(i)));
        discount.setType(phonecardType.get(i));
        Map<String, Float> discountRates = new HashMap<String, Float>();
        discountRates.put(PHONECARD_DISCOUNT_TYPE_1, convertDiscountRate(discountRate_1.get(i)));
        discountRates.put(PHONECARD_DISCOUNT_TYPE_5, convertDiscountRate(discountRate_5.get(i)));
        discountRates.put(PHONECARD_DISCOUNT_TYPE_10, convertDiscountRate(discountRate_10.get(i)));
        discountRates.put(PHONECARD_DISCOUNT_TYPE_20, convertDiscountRate(discountRate_20.get(i)));
        discount.setBuyDiscountRates(discountRates);
        discount.setSellerDiscountRate(convertDiscountRate(discountRateSeller.get(i)));

        phoneCardDiscounts.add(discount);
      }
    } catch (ServiceException serviceEx) {
      // TODO: update error code here.
      throw new GAEException("Could not connect to Google Docs service.", 1, serviceEx);
    } catch (IOException ioEx) {
      // TODO: update error code here.
      throw new GAEException("Something happened with Google Docs service.", 1, ioEx);
    } catch (NumberFormatException nfEx) {
      // TODO: update error code here.
      throw new GAEException("Discount percentage rate/price is in incorrect format.", 1, nfEx);
    } catch (IndexOutOfBoundsException indexEx) {
      // TODO: update error code here.
      throw new GAEException("Something missing in the spreadsheet table.", 1, indexEx);
    }
    return phoneCardDiscounts;
  }

  /**
   * The discount rate from Google Docs is in 'x.xx%' format. This helps to convert from that format to regular format.
   *
   * @param rate the rate in string format.
   * @return the rate in float format.
   */
  private static Float convertDiscountRate(String rate) {
    rate = rate.replace("%", "");
    return Float.parseFloat(rate);
  }

  private static Integer convertPhoneCardPrice(String price) {
    price = price.replace("₫", "").trim();
    return Integer.parseInt(price);
  }

}
