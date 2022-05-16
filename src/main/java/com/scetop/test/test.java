package com.scetop.test;


import com.scetop.face.Base64Util;
import com.scetop.face.FaceService;
import org.testng.annotations.Test;

import java.io.File;

public class test {
    @Test
    public void tets(){
        String path="C:\\Users\\wkj\\Desktop\\竞赛题目资料\\1000.webp";
       File f=new File(path);
       if(f.exists()) {
         String b = Base64Util.getImg(String.valueOf(f));
         String result = FaceService.add(b, String.valueOf(1), "usernamre:'张三");
         System.out.println("===================++++++++++");
         System.out.println(result);
           System.out.println("===================++++++++++");
       }else{
           System.out.println("文件不存在");
       }
    }
}
