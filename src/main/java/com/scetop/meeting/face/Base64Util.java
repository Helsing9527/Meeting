package com.scetop.meeting.face;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;

public class  Base64Util {
//    一个图片转换工具
    public static String getImg(String img){
        InputStream in = null;

        byte[] data = null;

        //读取图片字节数组

        try

        {

            in = new FileInputStream(img);

            data = new byte[in.available()];//读取数组知道没有read可以阻塞

            in.read(data);

            in.close();

        }

        catch (IOException e)

        {

            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data));
    }

}
