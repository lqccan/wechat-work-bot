package com.github.lqccan.wechat.work.bot.msg;

import lombok.Data;
import lombok.ToString;

/**
 * markdown类型消息
 */
@Data
@ToString
public class MarkdownMsg {

    /**
     * 内容
     */
    private String content;

}
