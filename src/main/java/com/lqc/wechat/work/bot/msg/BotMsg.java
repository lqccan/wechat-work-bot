package com.lqc.wechat.work.bot.msg;

/**
 * 最终包装发送给微信服务器的消息对象
 */
public class BotMsg {

    /**
     * 消息类型
     */
    private String msgtype;

    private TextMsg text;

    private MarkdownMsg markdown;

    private ImageMsg image;

    public BotMsg(TextMsg text) {
        this.text = text;
        this.msgtype = MsgType.TEXT;
    }

    public BotMsg(MarkdownMsg markdown) {
        this.markdown = markdown;
        this.msgtype = MsgType.MARKDOWN;
    }

    public BotMsg(ImageMsg imageMsg) {
        this.image = imageMsg;
        this.msgtype = MsgType.IMAGE;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public TextMsg getText() {
        return text;
    }

    public void setText(TextMsg text) {
        this.text = text;
    }

    public MarkdownMsg getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MarkdownMsg markdown) {
        this.markdown = markdown;
    }

    public ImageMsg getImage() {
        return image;
    }

    public void setImage(ImageMsg image) {
        this.image = image;
    }
}
