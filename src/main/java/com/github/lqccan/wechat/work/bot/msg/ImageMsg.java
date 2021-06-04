package com.github.lqccan.wechat.work.bot.msg;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片类型消息
 */
@Data
@ToString
public class ImageMsg {

    private String base64;

    private String md5;

    /**
     * 设置图片文件，改方法会自动转换设置base64和md5
     * @param file
     */
    public void setFile(File file) {
        if (file != null && file.exists() && file.exists()) {
            //文件存在
            byte[] data = null;
            // 读取图片字节数组
            try {
                InputStream in = new FileInputStream(file);
                data = new byte[in.available()];
                in.read(data);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            base64 = Base64.encode(data);
            md5 = DigestUtil.md5Hex(data);
        }
    }

    /**
     * 通过网上url的形式设置图片文件
     * 会先通过url下载图片文件之后进行文件发送
     * url不支持301或者302这种跳转到其他图片地址的形式
     * @param url
     */
    public void setFile(String url) {
        String suffix = url.substring(url.lastIndexOf('.'));
        File tempFile = FileUtil.createTempFile(System.currentTimeMillis()+"",suffix,FileUtil.getTmpDir(),true);
        HttpUtil.downloadFile(url,tempFile);
        setFile(tempFile);
    }

}
