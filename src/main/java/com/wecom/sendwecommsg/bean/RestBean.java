package com.wecom.sendwecommsg.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @Author : liuzhiyuan
 * @Create : 2023/12/8
 * Description :
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RestBean {
    private String time;
    private String retMsg;
}
