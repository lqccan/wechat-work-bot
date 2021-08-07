package com.github.lqccan.wechat.work.bot.msg;

import lombok.Data;
import lombok.ToString;

import java.io.File;

/**
 * 图片类型消息
 */
@Data
@ToString
public class ImageMsg {

    /**
     * 图片内容的base64编码
     */
    private String base64;

    /**
     * 图片内容（base64编码前）的md5值
     */
    private String md5;

    /**
     * 图片文件
     */
    private File file;

    /**
     * 图片url地址
     */
    private String url;

}
