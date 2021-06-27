package com.github.lqccan.wechat.work.bot.msg;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最终包装发送给微信服务器的消息对象
 */
@Data
@ToString
public class BotMsg {

    /**
     * 消息类型
     */
    private String msgtype;

    private TextMsg text;

    private MarkdownMsg markdown;

    private ImageMsg image;

    private Map<String, List<ArticleMsg>> news;

    private FileMsg file;

    public BotMsg(TextMsg textMsg) {
        if (textMsg.getContent().getBytes(StandardCharsets.UTF_8).length > 2048) {
            //文本内容，超过2048个字节，自动转为txt文件发送
            File file = FileUtil.createTempFile(FileUtil.getTmpDir(), true);
            file = FileUtil.rename(file, String.format("长消息%s.txt", DateUtil.now()), true);
            FileUtil.writeString(textMsg.getContent(), file, StandardCharsets.UTF_8);
            FileMsg fileMsg = new FileMsg();
            fileMsg.setFile(file);
            this.msgtype = MsgType.FILE;
            this.file = fileMsg;
        } else {
            this.msgtype = MsgType.TEXT;
            this.text = textMsg;
        }
    }

    public BotMsg(MarkdownMsg markdownMsg) {
        this.msgtype = MsgType.MARKDOWN;
        this.markdown = markdownMsg;
    }

    public BotMsg(ImageMsg imageMsg) {
        this.msgtype = MsgType.IMAGE;
        this.image = imageMsg;
    }

    public BotMsg(ArticleMsg articleMsg) {
        this.msgtype = MsgType.NEWS;
        List<ArticleMsg> articleMsgList = new ArrayList<ArticleMsg>();
        articleMsgList.add(articleMsg);
        this.news = new HashMap<String, List<ArticleMsg>>();
        this.news.put("articles", articleMsgList);
    }

    public BotMsg(List<ArticleMsg> articleMsgList) {
        this.msgtype = MsgType.NEWS;
        this.news = new HashMap<String, List<ArticleMsg>>();
        this.news.put("articles", articleMsgList);
    }

    public BotMsg(FileMsg fileMsg) {
        this.msgtype = MsgType.FILE;
        this.file = fileMsg;
    }

}
