package com.yihuan.util;


import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {
    
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //client salt
    private static final String salt = "1a2b3c4d";

    public static String inputPwdToFormPwd(String inputPwd){
        return md5(salt + inputPwd);
    }

    //server random salt
    public static String FormPwdToDBPwd(String inputPwd, String salt){
        return md5(salt + inputPwd);
    }

    //twice encoding
    public static String inputPwdToDBPwd(String inputPwd, String salt){
        String formPwd = inputPwdToFormPwd(inputPwd);
        return FormPwdToDBPwd(formPwd,salt);
    }

    public static void main(String[] args) {
        System.out.println(inputPwdToFormPwd("199596"));
        System.out.println(inputPwdToDBPwd("199596","1a2b3c4d"));
    }
}
