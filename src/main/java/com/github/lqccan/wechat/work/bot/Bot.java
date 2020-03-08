package com.github.lqccan.wechat.work.bot;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.github.lqccan.wechat.work.bot.exception.BotException;
import com.github.lqccan.wechat.work.bot.msg.*;

import java.util.Arrays;
import java.util.List;

/**
 * 企业微信机器人对象
 */
public class Bot {

    /**
     * json配置
     */
    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 企业微信群中获取的webhook地址
     */
    private String webhook;

    /**
     * 超时时间
     */
    private int timeout;

    public Bot(String webhook) {
        this.webhook = webhook;
        this.timeout = 5*1000;
    }

    public Bot(String webhook,int timeout) {
        this.webhook = webhook;
        this.timeout = timeout;
    }

    /**
     * 发送文本消息
     * @param msg
     */
    public void send(String msg) {
        send(msg,false);
    }

    /**
     * 发送文本消息
     * @param msg
     * @param atAll true：艾特所有人
     */
    public void send(String msg, boolean atAll) {
        TextMsg text = new TextMsg();
        text.setContent(msg);
        if (atAll) {
            text.setMentionedList(Arrays.asList("@all"));
        }
        BotMsg botMsg = new BotMsg(text);
        doPost(botMsg);
    }

    /**
     * 发送文本消息
     * @param msg
     */
    public void send(TextMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doPost(botMsg);
    }

    /**
     * 发送markdown消息
     * @param msg
     */
    public void send(MarkdownMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doPost(botMsg);
    }

    /**
     * 发送图片消息
     * @param msg
     */
    public void send(ImageMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doPost(botMsg);
    }

    /**
     * 发送图文消息
     * @param msg
     */
    public void send(ArticleMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doPost(botMsg);
    }

    /**
     * 发送多个图文消息
     * @param msg
     */
    public void send(List<ArticleMsg> msg) {
        BotMsg botMsg = new BotMsg(msg);
        doPost(botMsg);
    }

    /**
     * 请求微信接口，实现消息的发送
     * @param botMsg
     */
    public void doPost(BotMsg botMsg){
        try {
            String jsonStr = JSON.toJSONString(botMsg, config);
            String body = HttpRequest.post(webhook)
                    .header(Header.CONTENT_TYPE, ContentType.JSON.toString())
                    .body(jsonStr)
                    .timeout(timeout)
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            if (jsonObject.getInt("errcode") != 0) {
                throw new BotException(jsonObject.getInt("errcode")+" "+jsonObject.getStr("errmsg"));
            }
        } catch (Exception e) {
            throw new BotException(e.getMessage());
        }
    }

}

