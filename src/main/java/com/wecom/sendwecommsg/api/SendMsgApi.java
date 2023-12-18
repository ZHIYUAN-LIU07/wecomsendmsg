package com.wecom.sendwecommsg.api;

import com.alibaba.fastjson.JSON;
import com.wecom.sendwecommsg.bean.BodyToUrlBean;
import com.wecom.sendwecommsg.bean.ImageTextBean;
import com.wecom.sendwecommsg.bean.RestBean;
import com.wecom.sendwecommsg.service.SendMsgService;
import com.wecom.sendwecommsg.util.Base64Util;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author : liuzhiyuan
 * @Create : 2023/12/8
 * Description :
 */
@RequestMapping("/sendMsg")
@RestController
public class SendMsgApi {

    private final SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SendMsgService sendMsgService;

    @RequestMapping("/send")
    public String sendMsg(String text,
                          String webHookCore,
                          String listPhone,
                          String listName,
                          Boolean bas64Flag){

        if (Objects.isNull(bas64Flag)){
            bas64Flag = false;
        }
        if (bas64Flag){
            try {
                text = Base64Util.getBase64Dec(text);
            } catch (Exception e) {
                return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送失败，解码失败,cause:" + e.getMessage()));
            }
        }
        if (StringUtils.isBlank(text)){
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"文本内容空，发送失败"));
        }
        sendMsgService.send(text,webHookCore,listPhone,listName);
        return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送成功"));
    }

    @PostMapping("/sendPost")
    public String sendMsg(@RequestBody BodyToUrlBean s,
                          String webHookCore,
                          String listPhone,
                          String listName){
        if (Objects.isNull(s) || StringUtils.isBlank(s.getS())){
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"body内容空，发送失败"));
        }
        sendMsgService.send(s.getS(),webHookCore,listPhone,listName);
        return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送成功"));
    }

    /**
     * @param text:发送同时发送文本内容
     * @param webHookCore:发送机器人所属中心
     * @param file:发送文件
     * @param fileType:文件类型 默认:file 语音文件(仅支持AMR文件):传参值-voice,传输音频时需传参
     * @param listPhone:提醒成员手机号以逗号分割
     * @param listName:提醒成员姓名id以逗号分割
     * @param orrFileName:重写文件名
     */
    @PostMapping("/sendFile")
    public String sendFile(String text,
                           String webHookCore,
                           @RequestParam("file") MultipartFile file,
                           String fileType,
                           String listPhone,
                           String listName,
                           String orrFileName){
        if ( file == null || file.getSize() == 0){
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"文件不存在或者文件为空，发送失败"));
        }
        return sendMsgService.sendFile(file,text,webHookCore,listPhone,listName,fileType,orrFileName);
    }

    @PostMapping("/sendToMarkDown")
    public String sendToMarkDown(String base64text,
                                 String webHookCore){
        try {
            base64text = Base64Util.getBase64Dec(base64text);
        } catch (Exception e) {
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"发送失败,解码失败,cause:" + e.getMessage()));
        }
        if (StringUtils.isBlank(base64text)){
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"MarkDown文本内容空，发送失败"));
        }
        return sendMsgService.sendToMarkDown(base64text,webHookCore);
    }

    @PostMapping("/sendToImage")
    public String sendToMarkDown(@NotNull MultipartFile image, String webHookCore){
        if (image.getSize() == 0){
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"image不存在，发送失败"));
        }
        return sendMsgService.sendToImage(image,webHookCore);
    }

    @PostMapping("/sendToImageText")
    public String sendToImageText(@RequestBody List<ImageTextBean> imageAndTextList, String webHookCore){
        if (Objects.isNull(imageAndTextList) || imageAndTextList.size() < 1 || imageAndTextList.size() > 8){
            return JSON.toJSONString(new RestBean(formatter.format(new Date(System.currentTimeMillis())),"body为空或内容过多，发送失败"));
        }
        return sendMsgService.sendToNews(imageAndTextList,webHookCore);
    }

}
