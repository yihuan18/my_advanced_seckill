package com.yihuan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    public static final Pattern pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String str){
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
