package com.wecom.sendwecommsg.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Author : liuzhiyuan
 * @Create : 2023/12/14
 * Description :
 */
public class Base64Util {

    // 转码
    public static String getBase64Enc(String msg) {
        if (StringUtils.isBlank(msg)) {
            return "";
        }
        try {
            byte[] bytes = msg.getBytes("utf-8");
            msg = Base64.getEncoder().encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return msg;
    }

    // 解码
    public static String getBase64Dec(String msg) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(msg)) {
            return "";
        }
        byte[] decodedBytes = Base64.getDecoder().decode(msg);
        msg = new String(decodedBytes, "utf-8");
        return msg;
    }

    public static String getBase64Enc(byte[] msgByte) {
        String msg = "";
        if (msgByte.length == 0) {
            return "";
        }
        try {
            msg = Base64.getEncoder().encodeToString(msgByte);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return msg;
    }

    public static String calculateMD5(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        try (InputStream inputStream = file.getInputStream();
             DigestInputStream digestInputStream = new DigestInputStream(inputStream, MessageDigest.getInstance("MD5"))) {

            byte[] buffer = new byte[8192];
            while (digestInputStream.read(buffer) != -1) {
            }

            MessageDigest md = digestInputStream.getMessageDigest();
            byte[] md5Bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                sb.append(String.format("%02x", md5Byte));
            }

            return sb.toString();
        }
    }
}
