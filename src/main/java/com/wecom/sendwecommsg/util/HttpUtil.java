package com.wecom.sendwecommsg.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import okhttp3.*;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * @Author : liuzhiyuan
 * @Create : 2023/12/8
 * Description :
 */
@Component
public class HttpUtil {
    private static long timeOut;

    @Value("${timeOut}")
    public static void setTimeOut(long timeOut) {
        HttpUtil.timeOut = timeOut;
    }

    public static void sendMsgToGet(String url, Map<String,String> sendParam){
        Unirest.setTimeouts(timeOut,timeOut);
        StringBuilder sendUrl = new StringBuilder(url).append("?");
        if (sendParam.size() == 0){
            sendUrl.deleteCharAt(sendUrl.length()-1);
        }else {
            sendParam.forEach((k,y) -> sendUrl.append(k).append("=").append(y).append("&"));
            sendUrl.deleteCharAt(sendUrl.length()-1);
        }
        try {
            Unirest.get(sendUrl.toString())
                    .asString()
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMsgToPost(String url, String body){
        Unirest.setTimeouts(timeOut,timeOut);
        try {
            Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString()
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String sendMsgToPost(String url,
                                     InputStream inputStream,
                                     ContentType contentFileType,
                                     String fileNamePrefix,
                                     Map<String,String> headers,
                                     Map<String,String> body,
                                     String fileExtension){
        Unirest.setTimeouts(timeOut,timeOut);
        try {
            HttpRequestWithBody post = Unirest.post(url);
            headers.forEach(post :: field);
            body.forEach(post :: header);
            return post.field("media",inputStream,contentFileType,fileNamePrefix+fileExtension)
                    .asString()
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    public static String sendMsgPostOkHttp(String url,
                                           String ContentType,
                                           InputStream inputStream,
                                           MediaType contentFileType,
                                           String fileNamePrefix,
                                           Map<String,String> headers,
                                           Map<String,String> body,
                                           String fileExtension) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        byte[] fileBytes = convertInputStreamToByteArray(inputStream);
        RequestBody requestFileBody = RequestBody.create(fileBytes,contentFileType);
        MultipartBody.Builder media = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("media", fileNamePrefix + fileExtension, requestFileBody);
        body.forEach(media :: addFormDataPart);
        headers.forEach(media :: addFormDataPart);
        MultipartBody allBody = media.build();

        Request request = new Request.Builder()
                .url(url)
                .method("POST", allBody)
                .addHeader("Content-Type", ContentType)
                .build();

        return Objects.requireNonNull(client.newCall(request).execute().body()).string();

    }
    public static byte[] convertInputStreamToByteArray(InputStream inputStream)  {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
//            Path tempFile = Files.createTempFile(fileNamePrefix, fileExtension);
//            Files.write(tempFile, byteArrayOutputStream.toByteArray(), StandardOpenOption.CREATE);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
                if (byteArrayOutputStream != null){
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
