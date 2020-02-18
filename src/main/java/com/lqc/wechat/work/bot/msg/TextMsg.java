package com.lqc.wechat.work.bot.msg;

import java.util.List;

/**
 * 文字类型消息
 */
public class TextMsg{

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMentionedList() {
        return mentionedList;
    }

    public void setMentionedList(List<String> mentionedList) {
        this.mentionedList = mentionedList;
    }

    public List<String> getMentionedMobileList() {
        return mentionedMobileList;
    }

    public void setMentionedMobileList(List<String> mentionedMobileList) {
        this.mentionedMobileList = mentionedMobileList;
    }

}
