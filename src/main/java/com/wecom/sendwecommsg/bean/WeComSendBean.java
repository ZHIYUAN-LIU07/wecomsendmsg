package com.wecom.sendwecommsg.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author : liuzhiyuan
 * @Create : 2023/12/8
 * Description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WeComSendBean<T> {
    private String msgtype;
    private T sengMsgBeanTypeFlag;
}
