package com.ihgoo.allinone.util;

import java.io.UnsupportedEncodingException;

import android.util.Base64;

import com.ihgoo.allinone.qiniu.common.Config;

public final class UrlSafeBase64 {
    public static String encodeToString(String data) {
        try {
            return encodeToString(data.getBytes(Config.CHARSET));
        } catch (UnsupportedEncodingException e) {
            //never in
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeToString(byte[] data) {
        return Base64.encodeToString(data, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static byte[] decode(String data) {
        return Base64.decode(data, Base64.URL_SAFE | Base64.NO_WRAP);
    }
}
