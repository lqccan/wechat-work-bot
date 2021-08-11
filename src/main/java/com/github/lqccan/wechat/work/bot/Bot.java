package com.github.lqccan.wechat.work.bot;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.lqccan.wechat.work.bot.exception.BotException;
import com.github.lqccan.wechat.work.bot.msg.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * 企业微信机器人对象
 */
public class Bot {

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
     * HttpClient http请求
     */
    private CloseableHttpClient httpClient;

    public Bot(String webhook) {
        if (webhook == null) {
            throw new BotException("webhook cannot be null");
        }
        this.webhook = webhook;
        this.uploadUrl = webhook.replace("send", "upload_media") + "&type=file";
        this.objectMapper = new ObjectMapper();
        try {
            // 2.12版本之后PropertyNamingStrategy使用新的PropertyNamingStrategies代替
            // 为了兼容老版本，优先使用PropertyNamingStrategies，类不存在则使用老版本PropertyNamingStrategy
            Class.forName("com.fasterxml.jackson.databind.PropertyNamingStrategies");
            this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        } catch (ClassNotFoundException e) {
            this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }
        this.httpClient = HttpClients.createDefault();
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
     * 处理消息
     *
     * @param botMsg
     */
    private void handleMsg(BotMsg botMsg) {
        if (MsgType.FILE.equals(botMsg.getMsgtype()) && botMsg.getFile() != null) {
            FileMsg file = botMsg.getFile();
            if (file.getMediaId() == null) {
                //mediaId为空，先上传文件获取mediaId
                String mediaId = this.getMediaId(file.getFile(), file.getFileName());
                file.setMediaId(mediaId);
            }
        } else if (MsgType.IMAGE.equals(botMsg.getMsgtype()) && botMsg.getImage() != null) {
            ImageMsg image = botMsg.getImage();
            File file = image.getFile();
            boolean clearFile = false;
            if (file == null && image.getUrl() != null) {
                //网络图片，先根据url地址下载图片
                String suffix = image.getUrl().substring(image.getUrl().lastIndexOf('.'));
                file = FileUtil.createTempFile(System.currentTimeMillis() + "", suffix, FileUtil.getTmpDir(), true);
                HttpGet httpGet = new HttpGet(image.getUrl());
                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    if (response.getStatusLine().getStatusCode() != 200) {
                        throw new BotException(response.toString());
                    }
                    FileUtil.writeFromStream(response.getEntity().getContent(), file);
                    clearFile = true;
                } catch (IOException e) {
                    throw new BotException(e);
                }
            }
            if (file != null && file.exists()) {
                //读取图片文件字节并进行编码
                byte[] data = null;
                try {
                    InputStream in = new FileInputStream(file);
                    data = new byte[in.available()];
                    in.read(data);
                    in.close();
                } catch (IOException e) {
                    throw new BotException(e);
                }
                image.setBase64(Base64.encode(data));
                image.setMd5(DigestUtil.md5Hex(data));
                //清理文件
                if (clearFile) {
                    file.delete();
                }
            }
        }
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
        HttpPost httpPost = new HttpPost(uploadUrl);
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .setContentType(ContentType.create("multipart/form-data"))
                .setMode(HttpMultipartMode.RFC6532)
                .addBinaryBody("media", file, ContentType.create("application/octet-stream"), fileName)
                .build();
        httpPost.setEntity(httpEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BotException(response.toString());
            }
            ResMsg res = objectMapper.readValue(response.getEntity().getContent(), ResMsg.class);
            if (res.getErrcode() != 0) {
                throw new BotException(res.getErrmsg());
            }
            return res.getMediaId();
        } catch (IOException e) {
            throw new BotException(e);
        }
    }

    /**
     * 请求微信接口，实现消息的发送
     *
     * @param botMsg
     */
    public void doSend(BotMsg botMsg) {
        this.handleMsg(botMsg);
        //请求微信接口发送消息
        String botJson;
        try {
            botJson = objectMapper.writeValueAsString(botMsg);
        } catch (JsonProcessingException ex) {
            throw new BotException(ex);
        }
        HttpPost httpPost = new HttpPost(webhook);
        HttpEntity httpEntity = new StringEntity(botJson, ContentType.create("application/json", StandardCharsets.UTF_8));
        httpPost.setEntity(httpEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BotException(response.toString());
            }
            ResMsg res = objectMapper.readValue(response.getEntity().getContent(), ResMsg.class);
            if (res.getErrcode() != 0) {
                throw new BotException(res.getErrmsg());
            }
        } catch (IOException e) {
            throw new BotException(e);
        }
    }

}

