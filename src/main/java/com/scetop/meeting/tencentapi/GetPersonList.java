package com.scetop.meeting.tencentapi;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.iai.v20200303.IaiClient;
import com.tencentcloudapi.iai.v20200303.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetPersonList {
    @Autowired
    private Environment env;

    public List<String> getPersonList() {
        List<String> personIds = new ArrayList<>();
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
            GetPersonListRequest req = new GetPersonListRequest();
            req.setGroupId("Face");
            // 返回的resp是一个GetPersonListResponse的实例，与请求对象对应
            GetPersonListResponse resp = client.GetPersonList(req);
            // 输出json格式的字符串回包
//            System.out.println(GetPersonListResponse.toJsonString(resp));
            // 遍历人员列表
            PersonInfo[] personInfos = resp.getPersonInfos();
            for (PersonInfo personInfo : personInfos) {
                // 获取人员id并存入List集合
                String personId = personInfo.getPersonId();
                personIds.add(personId);
            }
            return personIds;
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            return null;
        }
    }
}