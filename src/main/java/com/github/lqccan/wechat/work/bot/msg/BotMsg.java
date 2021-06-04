package com.github.lqccan.wechat.work.bot.msg;

import lombok.Data;
import lombok.ToString;

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

    public BotMsg(TextMsg text) {
        this.msgtype = MsgType.TEXT;
        this.text = text;
    }

    public BotMsg(MarkdownMsg markdown) {
        this.msgtype = MsgType.MARKDOWN;
        this.markdown = markdown;
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
