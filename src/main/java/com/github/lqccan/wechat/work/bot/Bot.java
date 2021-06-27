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

import java.util.Collections;
import java.util.List;

/**
 * 企业微信机器人对象
 */
public class Bot {

    /**
     * json配置成SnakeCase模式
     */
    private static final SerializeConfig CONFIG;

    static {
        CONFIG = new SerializeConfig();
        CONFIG.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 企业微信群中获取的webhook地址
     */
    private String webhook;

    /**
     * 超时时间（默认5秒）
     */
    private int timeout = 5*1000;

    public Bot(String webhook) {
        this.webhook = webhook;
    }

    public Bot(String webhook, int timeout) {
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
            text.setMentionedList(Collections.singletonList("@all"));
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
     * 发送文件消息
     * @param msg
     */
    public void send(FileMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doPost(botMsg);
    }

    /**
     * 请求微信接口，实现消息的发送
     * @param botMsg
     */
    public void doPost(BotMsg botMsg){
        try {
            FileMsg file = botMsg.getFile();
            if (file != null && file.getMediaId() == null) {
                //文件类型消息，且mediaId为空，则先上传文件获取mediaId
                try {
                    String upload = webhook.replace("send", "upload_media") + "&type=file";
                    String body = HttpRequest.post(upload)
                            .header(Header.CONTENT_TYPE, ContentType.MULTIPART.toString())
                            .form("media", file.getFile())
                            .timeout(timeout)
                            .execute()
                            .body();
                    JSONObject jsonObject = JSONUtil.parseObj(body);
                    if (jsonObject.getInt("errcode") != 0) {
                        throw new BotException(jsonObject.getInt("errcode") + " " + jsonObject.getStr("errmsg"));
                    } else {
                        file.setMediaId(jsonObject.getStr("media_id"));
                    }
                } catch (Exception e) {
                    throw new BotException(e.getMessage());
                }
            }
            //请求微信接口发送消息
            String jsonStr = JSON.toJSONString(botMsg, CONFIG);
            String body = HttpRequest.post(webhook)
                    .header(Header.CONTENT_TYPE, ContentType.JSON.toString())
                    .body(jsonStr)
                    .timeout(timeout)
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            if (jsonObject.getInt("errcode") != 0) {
                throw new BotException(jsonObject.getInt("errcode") + " " + jsonObject.getStr("errmsg"));
            }
        } catch (BotException be) {
            throw be;
        } catch (Exception e) {
            throw new BotException(e.getMessage());
        }
    }

}

