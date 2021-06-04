package com.lqc;

import com.github.lqccan.wechat.work.bot.Bot;
import com.github.lqccan.wechat.work.bot.msg.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
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
        textMsg.setContent("文字发送\nhttp://www.baidu.com");
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
        imageMsg.setFile(new File("src/test/0.png"));
        bot.send(imageMsg);
        //网络文件
        imageMsg.setFile("http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png");
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
        articleMsg.setUrl("https://www.baidu.com");
        articleMsg.setPicurl("http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png");
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
            articleMsg.setUrl("https://www.baidu.com");
            articleMsg.setPicurl("http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png");
            list.add(articleMsg);
        }
        bot.send(list);
    }

    /**
     * 文件测试
     */
    @Test
    public void fileTest() {
        FileMsg fileMsg = new FileMsg();
        //本地文件
        fileMsg.setFile(new File("src/test/1.xlsx"));
        bot.send(fileMsg);
    }

    /**
     * 全部测试
     * @throws Exception
     */
    @Test
    public void allTest() throws Exception {
        textTest();
        Thread.sleep(1000);
        markdownTest();
        Thread.sleep(1000);
        imageTest();
        Thread.sleep(1000);
        articleTest();
        Thread.sleep(1000);
        articleListTest();
        Thread.sleep(1000);
        fileTest();
        Thread.sleep(1000);
    }

}
