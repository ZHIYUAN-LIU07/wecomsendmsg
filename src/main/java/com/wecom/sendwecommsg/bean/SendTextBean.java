package com.wecom.sendwecommsg.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SendTextBean {
    private String content;
    private String[] mentioned_list;
    private String[] mentioned_mobile_list;
}
