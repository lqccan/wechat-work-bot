package com.github.lqccan.wechat.work.bot.msg;

import lombok.Data;
import lombok.ToString;

/**
 * 图文类型消息
 */
@Data
@ToString
public class ArticleMsg {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 链接
     */
    private String url;

    /**
     * 图片链接
     */
    private String picurl;

}