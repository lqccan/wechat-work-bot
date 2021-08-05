package com.github.lqccan.wechat.work.bot.msg;

import lombok.Data;
import lombok.ToString;

import java.io.File;

/**
 * 文件类型消息
 */
@Data
@ToString
public class FileMsg {

    /**
     * 文件id
     */
    private String mediaId;

    /**
     * 文件
     */
    private File file;

    /**
     * 显示文件名（为空则默认使用file字段的文件名）
     */
    private String fileName;

}
