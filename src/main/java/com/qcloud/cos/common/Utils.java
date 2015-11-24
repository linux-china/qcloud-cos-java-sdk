package com.qcloud.cos.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * utils
 *
 * @author linux_china
 */
public class Utils {
    /**
     * url encode the input
     *
     * @param text text
     * @return encoded text
     */
    public static String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (Exception ignore) {
            return text;
        }
    }

    /**
     * get file sha1hex
     *
     * @param localFile local fle
     * @return sha1hex
     * @throws IOException IO exception
     */
    public static String getFileSha1(File localFile) throws IOException {
        FileInputStream fis = new FileInputStream(localFile);
        try {
            return DigestUtils.sha1Hex(fis);
        } finally {
            fis.close();
        }
    }
}
