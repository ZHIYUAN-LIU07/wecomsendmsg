package com.wecom.sendwecommsg.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Author : liuzhiyuan
 * @Create : 2023/12/8
 * Description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SendNewsBean {
    private List<ImageTextBean> articles;
}
