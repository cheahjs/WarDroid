package com.deathsnacks.wardroid.utils;

/**
 * Created by Admin on 04/02/14.
 */
public class PreferenceUtils {
    private static final String SEP = "|";

    private static Boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String[] fromPersistedPreferenceValue(String val) {
        if (isEmpty(val)) {
            return new String[0];
        } else {
            return val.split("\\" + SEP);
        }
    }

    public static String toPersistedPreferenceValue(CharSequence... entryKeys) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entryKeys.length; i++) {
            sb.append(entryKeys[i]);
            if (i < entryKeys.length - 1) {
                sb.append(SEP);
            }
        }
        return sb.toString();
    }
}
