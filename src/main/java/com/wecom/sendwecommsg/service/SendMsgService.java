package com.wecom.sendwecommsg.service;

import com.alibaba.fastjson.JSON;
import com.wecom.sendwecommsg.bean.*;
import com.wecom.sendwecommsg.util.Base64Util;
import com.wecom.sendwecommsg.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author : liuzhiyuan
 * @Create : 2023/12/8
 * Description :
 */
@Service
@Slf4j
public class SendMsgService {

    private final SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("${defRoBot}")
    private String webHookCb;

    @Value("${oneRoBot}")
    private String webHookLp;

    @Value("${twolRoBot}")
    private String webHookConsul;

    private synchronized String init(String webHookInit){
        if (StringUtils.isBlank(webHookInit)){
            webHookInit = "def";
        }
        switch (webHookInit) {
            case "def":
                webHookInit = webHookCb;
                break;
            case "one":
                webHookInit = webHookLp;
                break;
            case "two":
                webHookInit = webHookConsul;
                break;
        }
        return webHookInit;
    }
    public void send(String text,String webHookCore,String listPhone, String listName) {
        String webHookUrl = init(webHookCore);

        if (StringUtils.isBlank(listName)){
            listName = "";
        }
        if (StringUtils.isBlank(listPhone)){
            listPhone = "";
        }
        String[] allPhone = listPhone.split(",");
        String[] allName = listName.split(",");

        WeComSendBean<SendTextBean> weComSendBean = new WeComSendBean<>(FinalBean.TEXT,new SendTextBean(text,allName,allPhone));
        String sendBody = JSON.toJSONString(weComSendBean);
        sendBody = sendBody.replaceAll(FinalBean.SENDMSGBEANTYPEFLAG, FinalBean.TEXT);

        HttpUtil.sendMsgToPost(webHookUrl,sendBody);
        String formatDate = formatter.format(new Date(System.currentTimeMillis()));
        log.info("SENDOK,当前时间:{}",formatDate);
    }

    public String sendFile(MultipartFile file, String text, String webHookCore, String listPhone, String listName,String fileType,String orrFileName) {

        String webHookUrl = init(webHookCore);

        InputStream fileInputStream = null;
        String fileNamePrefix = "";
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        if (StringUtils.isBlank(fileType)){
            fileType = "file";
        }
        if (StringUtils.isBlank(orrFileName)){
            fileNamePrefix = file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf("."));
        }else {
            fileNamePrefix = orrFileName;
        }
        int lastWebHookKeyIndex = webHookUrl.lastIndexOf('?');
        String key = "";
        if (lastWebHookKeyIndex != -1) {
            // 提取问号之后的内容
            key = webHookUrl.substring(lastWebHookKeyIndex + 5);
        }
        String uploadFileUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?key=" + key + "&type=" + fileType;
        try {
            fileInputStream = file.getInputStream();
            String contentType = file.getContentType();
            Map<String,String> headersMsg = new HashMap<>();
            headersMsg.put("Content-Type",contentType);
            Map<String,String> bodyMsg = new HashMap<>();
            bodyMsg.put("filelength",String.valueOf(file.getSize()));
            headersMsg.put("filename",fileNamePrefix + fileExtension);
            assert contentType != null;
            String uploadBody = HttpUtil.sendMsgPostOkHttp(uploadFileUrl, contentType, fileInputStream, MediaType.parse(contentType), fileNamePrefix, headersMsg, bodyMsg, fileExtension);
            if (StringUtils.isBlank(uploadBody) || !uploadBody.contains("\"errcode\":0")){
                log.error("上传文件错误");
                return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送文件失败"));
            }
            FileBean fileBean = JSON.parseObject(uploadBody, FileBean.class);
            String media_id = fileBean.getMedia_id();
            String sendFileMsgBody = "{\"msgtype\":\"file\",\"file\":{\"media_id\":\""+ media_id + "\"}";
            HttpUtil.sendMsgToPost(webHookUrl,sendFileMsgBody);
            String formatDate = formatter.format(new Date(System.currentTimeMillis()));
            log.info("SENDOK,发送文件成功,当前时间:{}",formatDate);
            if (StringUtils.isBlank(text)){
                return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送文件成功"));
            }
            if (StringUtils.isBlank(listPhone)){
                listPhone = "";
            }
            if (StringUtils.isBlank(listName)){
                listName = "";
            }
            String[] allPhone = listPhone.split(",");
            String[] allName = listName.split(",");

            WeComSendBean<SendTextBean> weComSendBean = new WeComSendBean<>(FinalBean.TEXT,new SendTextBean(text,allName,allPhone));
            String sendBody = JSON.toJSONString(weComSendBean);
            sendBody = sendBody.replaceAll(FinalBean.SENDMSGBEANTYPEFLAG, FinalBean.TEXT);

            HttpUtil.sendMsgToPost(webHookUrl,sendBody);
            String formatDateToText = formatter.format(new Date(System.currentTimeMillis()));
            log.info("SENDOK,当前时间:{},发送text文本",formatDateToText);
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送文件成功"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendToMarkDown(String text, String webHookCore) {

        String webHookUrl = init(webHookCore);

        WeComSendBean<SendMarkdownBean> weComSendBean = new WeComSendBean<>(FinalBean.MARKDOWN,new SendMarkdownBean(text));
        String sendBody = JSON.toJSONString(weComSendBean);
        sendBody = sendBody.replaceAll(FinalBean.SENDMSGBEANTYPEFLAG, FinalBean.MARKDOWN);

        HttpUtil.sendMsgToPost(webHookUrl,sendBody);

        String formatDateToTetx = formatter.format(new Date(System.currentTimeMillis()));
        log.info("SENDOK,当前时间:{},发送markdown文本",formatDateToTetx);
        return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送markdown文本成功"));
    }

    public String sendToImage(MultipartFile image, String webHookCore) {

        String base64ImageData = "";
        String md5ImageToBase64BeFore = "";

        try {
            base64ImageData = Base64Util.getBase64Enc(image.getBytes());
            md5ImageToBase64BeFore = Base64Util.calculateMD5(image);
        } catch (Exception e) {
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送失败,解码失败,cause:" + e.getMessage()));
        }
        if (StringUtils.isBlank(base64ImageData)){
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"image不存在，发送失败"));
        }

        String webHookUrl = init(webHookCore);

        WeComSendBean<SendImageBean> weComSendBean = new WeComSendBean<>(FinalBean.IMAGE,new SendImageBean(base64ImageData,md5ImageToBase64BeFore));
        String sendBody = JSON.toJSONString(weComSendBean);
        sendBody = sendBody.replaceAll(FinalBean.SENDMSGBEANTYPEFLAG, FinalBean.IMAGE);

        HttpUtil.sendMsgToPost(webHookUrl,sendBody);

        String formatDateToTetx = formatter.format(new Date(System.currentTimeMillis()));
        log.info("SENDOK,当前时间:{},发送image",formatDateToTetx);
        return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送imge成功"));
    }

    public String sendToNews(List<ImageTextBean> imageAndTextList ,String webHookCore) {

        String webHookUrl = init(webHookCore);

        WeComSendBean<SendNewsBean> weComSendBean = new WeComSendBean<>(FinalBean.NEWS,new SendNewsBean(imageAndTextList));
        String sendBody = JSON.toJSONString(weComSendBean);
        sendBody = sendBody.replaceAll(FinalBean.SENDMSGBEANTYPEFLAG, FinalBean.NEWS);

        HttpUtil.sendMsgToPost(webHookUrl,sendBody);

        String formatDateToTetx = formatter.format(new Date(System.currentTimeMillis()));
        log.info("SENDOK,当前时间:{},发送image",formatDateToTetx);
        return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送imge成功"));

    }

    public static void main(String[] args) {

    }
}
