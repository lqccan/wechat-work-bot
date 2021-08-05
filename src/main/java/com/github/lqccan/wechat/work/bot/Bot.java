package com.github.lqccan.wechat.work.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.github.lqccan.wechat.work.bot.exception.BotException;
import com.github.lqccan.wechat.work.bot.msg.*;
import okhttp3.*;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 企业微信机器人对象
 */
public class Bot {

    /**
     * 默认超时时间（5秒）
     */
    private static final long DEFAULT_TIMEOUT = 5 * 1000L;

    /**
     * 企业微信群中获取的webhook地址
     */
    private String webhook;

    /**
     * 文件上传地址
     */
    private String uploadUrl;

    /**
     * ObjectMapper json解析
     */
    private ObjectMapper objectMapper;

    /**
     * OkHttpClient http请求
     */
    private OkHttpClient okHttpClient;

    public Bot(String webhook) {
        this(webhook, null);
    }

    public Bot(String webhook, Long timeout) {
        if (webhook == null) {
            throw new BotException("webhook cannot be null");
        }
        this.webhook = webhook;
        if (timeout == null || timeout <= 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        this.uploadUrl = webhook.replace("send", "upload_media") + "&type=file";
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .callTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 发送文本消息
     *
     * @param msg
     */
    public void send(String msg) {
        send(msg, false);
    }

    /**
     * 发送文本消息
     *
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
        doSend(botMsg);
    }

    /**
     * 发送文本消息
     *
     * @param msg
     */
    public void send(TextMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doSend(botMsg);
    }

    /**
     * 发送markdown消息
     *
     * @param msg
     */
    public void send(MarkdownMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doSend(botMsg);
    }

    /**
     * 发送图片消息
     *
     * @param msg
     */
    public void send(ImageMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doSend(botMsg);
    }

    /**
     * 发送图文消息
     *
     * @param msg
     */
    public void send(ArticleMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doSend(botMsg);
    }

    /**
     * 发送多个图文消息
     *
     * @param msg
     */
    public void send(List<ArticleMsg> msg) {
        BotMsg botMsg = new BotMsg(msg);
        doSend(botMsg);
    }

    /**
     * 发送文件消息
     *
     * @param msg
     */
    public void send(FileMsg msg) {
        BotMsg botMsg = new BotMsg(msg);
        doSend(botMsg);
    }

    /**
     * 上传文件到微信服务器，获取mediaId
     *
     * @param file
     * @param fileName
     * @return
     */
    private String getMediaId(File file, String fileName) {
        if (fileName == null || "".equals(fileName.trim())) {
            fileName = file.getName();
        }
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("media", fileName, fileBody)
                .build();
        Request request = new Request.Builder()
                .url(uploadUrl)
                .addHeader("Content-Type", "multipart/form-data")
                .post(multipartBody)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BotException(response.toString());
            }
            ResMsg res = objectMapper.readValue(response.body().byteStream(), ResMsg.class);
            if (res.getErrcode() != 0) {
                throw new BotException(res.getErrmsg());
            }
            return res.getMediaId();
        } catch (BotException be) {
            throw be;
        } catch (Exception e) {
            throw new BotException(e);
        }
    }

    /**
     * 请求微信接口，实现消息的发送
     *
     * @param botMsg
     */
    public void doSend(BotMsg botMsg) {
        FileMsg file = botMsg.getFile();
        if (file != null && file.getMediaId() == null) {
            //文件类型消息，且mediaId为空，则先上传文件获取mediaId
            String mediaId = this.getMediaId(file.getFile(), file.getFileName());
            file.setMediaId(mediaId);
        }
        //请求微信接口发送消息
        String botJson = null;
        try {
            botJson = objectMapper.writeValueAsString(botMsg);
        } catch (JsonProcessingException ex) {
            throw new BotException(ex);
        }
        RequestBody requestBody = RequestBody.create(botJson, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(webhook)
                .post(requestBody)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BotException(response.toString());
            }
            ResMsg res = objectMapper.readValue(response.body().byteStream(), ResMsg.class);
            if (res.getErrcode() != 0) {
                throw new BotException(res.getErrmsg());
            }
        } catch (BotException be) {
            throw be;
        } catch (Exception e) {
            throw new BotException(e);
        }
    }

}

