package com.scetop.meeting.tencentapi.person;

import com.scetop.meeting.pojo.User;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.iai.v20200303.IaiClient;
import com.tencentcloudapi.iai.v20200303.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ModifyPersonBaseInfo {
    @Autowired
    private Environment env;

    public void modifyPersonBaseInfo(User user) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(env.getProperty("SecretId"), env.getProperty("SecretKey"));
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, "ap-chengdu", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            ModifyPersonBaseInfoRequest req = new ModifyPersonBaseInfoRequest();
            req.setPersonId(user.getId().toString());
            req.setPersonName(user.getName());
            req.setGender(user.getGender());
            // 返回的resp是一个ModifyPersonBaseInfoResponse的实例，与请求对象对应
            ModifyPersonBaseInfoResponse resp = client.ModifyPersonBaseInfo(req);
            // 输出json格式的字符串回包
//            System.out.println(ModifyPersonBaseInfoResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}