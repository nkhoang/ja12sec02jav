package com.nkhoang.gea.mock;


public class LMSUtil {
    public static String standardizeAddress(Address address) {
        String result = null;
        // do some proces here
        result = address.getAddress() + " " + address.getCity();

        return result;
    }

    public static boolean canStandardize(Address address) {
        boolean result = false;
        // do something here.
        if (address != null) {
            // do something here
            result = true;
        }

        return result;
    }

	public static String getRandomValue(String param1, String param2) {
		return param1 + param2;
	}

	public static String getRandomValueForFun(String param) {
		String randomValue = getRandomValue(param + "1" , param + "2");

		return randomValue + "_param";
	}
}
