package com.github.lqccan.wechat.work.bot.msg;

import lombok.Data;
import lombok.ToString;

/**
 * 微信返回结果消息
 */
@Data
@ToString
public class ResMsg{

    /**
     * code==0表示请求成功
     */
    private int errcode;

    private String errmsg;

    private String type;

    private String mediaId;

    private String createdAt;

}
