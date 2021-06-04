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

    private String mediaId;

    private File file;

}
