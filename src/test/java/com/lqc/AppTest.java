package com.lqc;

import com.github.lqccan.wechat.work.bot.Bot;
import com.github.lqccan.wechat.work.bot.msg.ArticleMsg;
import com.github.lqccan.wechat.work.bot.msg.ImageMsg;
import com.github.lqccan.wechat.work.bot.msg.MarkdownMsg;
import com.github.lqccan.wechat.work.bot.msg.TextMsg;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AppTest {

    private Bot bot;

    /**
     * 构造机器人对象
     */
    @Before
    public void buildBot() {
        bot = new Bot("这里填自己的webhook地址");
    }

    /**
     * 文字测试
     */
    @Test
    public void textTest() {
        TextMsg textMsg = new TextMsg();
        textMsg.setContent("文字发送\nhttp://www.3kkg.com");
        bot.send(textMsg);
    }

    /**
     * markdown测试
     */
    @Test
    public void markdownTest() {
        MarkdownMsg markdownMsg = new MarkdownMsg();
        markdownMsg.setContent("实时新增用户反馈<font color=\"warning\">132例</font>，请相关同事注意。\n" +
                "         >类型:<font color=\"comment\">用户反馈</font>\n" +
                "         >普通用户反馈:<font color=\"comment\">117例</font>\n" +
                "         >VIP用户反馈:<font color=\"comment\">15例</font>");
        bot.send(markdownMsg);
    }

    /**
     * 图片测试
     */
    @Test
    public void imageTest() {
        ImageMsg imageMsg = new ImageMsg();
        //本地文件
//        imageMsg.setFile(new File("/Users/xxx.png"));
        //网络文件
        imageMsg.setFile("https://i.loli.net/2020/02/04/znKwNdcSujE2i4l.png");
        bot.send(imageMsg);
    }

    /**
     * 图文测试
     */
    @Test
    public void articleTest() {
        ArticleMsg articleMsg = new ArticleMsg();
        articleMsg.setTitle("图文");
        articleMsg.setDescription("这是一条图文消息");
        articleMsg.setUrl("https://www.3kkg.com");
        articleMsg.setPicurl("https://i.loli.net/2020/02/04/znKwNdcSujE2i4l.png");
        bot.send(articleMsg);
    }

    /**
     * 多个图文测试
     */
    @Test
    public void articleListTest() {
        List<ArticleMsg> list = new ArrayList<ArticleMsg>();
        for (int i = 1; i <= 3; i++) {
            ArticleMsg articleMsg = new ArticleMsg();
            articleMsg.setTitle("图文"+i);
            articleMsg.setDescription("这是一条图文消息");
            articleMsg.setUrl("https://www.3kkg.com");
            articleMsg.setPicurl("https://i.loli.net/2020/02/04/znKwNdcSujE2i4l.png");
            list.add(articleMsg);
        }
        bot.send(list);
    }

}
