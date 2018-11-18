package com.impakter.seller.utils;

/**
 * Created by Minh Toan on 01/02/2018.
 */

public class NumberUtil {
    //Math.round(4.4654545*10)/10
    public static String formatFloatNumber(float number) {
        if (number >= 1000 && number < 1000000) {
            return Math.round((number / 1000F) * 10) / 10F + "K";
        } else if (number >= 1000000 && number < 1000000000) {
            return Math.round((number / 1000000F) * 10) / 10F + "M";
        } else if (number >= 1000000000) {
            return Math.round((number / 1000000000F) * 10) / 10F + "B";
        }
            return number + "";
    }
    public static String formatIntNumber(int number) {
        if (number >= 1000 && number < 1000000) {
            return Math.round((number / 1000F) * 10) / 10F + "K";
        } else if (number >= 1000000 && number < 1000000000) {
            return Math.round((number / 1000000F) * 10) / 10F + "M";
        } else if (number >= 1000000000) {
            return Math.round((number / 1000000000F) * 10) / 10F + "B";
        }
        return number + "";
    }
}
