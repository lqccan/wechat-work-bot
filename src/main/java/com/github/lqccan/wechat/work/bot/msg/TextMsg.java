package com.github.lqccan.wechat.work.bot.msg;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 文字类型消息
 */
@Data
@ToString
public class TextMsg {

    /**
     * 内容
     */
    private String content;

    /**
     * userid列表
     */
    private List<String> mentionedList;

    /**
     * 手机号列表列表
     */
    private List<String> mentionedMobileList;

}
