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
public class ImageTextBean {
    private String title;
    private String description;
    private String url;
    private String picurl;
}
