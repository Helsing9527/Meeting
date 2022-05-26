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
public class CreatePerson {
    @Autowired
    private Environment env;

    public String register(User user) {
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
            CreatePersonRequest req = new CreatePersonRequest();
            // 待加入的人员库ID，取值为创建人员库接口中的GroupId
            req.setGroupId("Face");
            // 人员名称。[1，60]个字符，可修改，可重复。
            req.setPersonName(user.getName());
            // 人员ID，单个腾讯云账号下不可修改，不可重复。支持英文、数字、-%@#&_，长度限制64B。
            req.setPersonId(user.getId().toString());
            // 0代表未填写，1代表男性，2代表女性。
            req.setGender(user.getGender());
            // 图片 base64 数据，base64 编码后大小不可超过5M。 jpg格式长边像素不可超过4000，其他格式图片长边像素不可超2000。 支持PNG、JPG、JPEG、BMP，不支持 GIF 图片。
            req.setImage(user.getBase64());
            /**
             * 此参数用于控制判断 Image 或 Url 中图片包含的人脸，是否在人员库中已有疑似的同一人。 如果判断为已有相同人在人员库中，
             * 则不会创建新的人员，返回疑似同一人的人员信息。 如果判断没有，则完成创建人员。 0: 不进行判断，无论是否有疑似同一人在
             * 库中均完成入库； 1:较低的同一人判断要求（百一误识别率）； 2: 一般的同一人判断要求（千一误识别率）； 3: 较高的同一
             * 人判断要求（万一误识别率）； 4: 很高的同一人判断要求（十万一误识别率）。 默认 0。
             * 注： 要求越高，则疑似同一人的概率越小。不同要求对应的误识别率仅为参考值，您可以根据实际情况调整。
             */
            req.setUniquePersonControl(2L);
            /**
             * 图片质量控制。 0: 不进行控制； 1:较低的质量要求，图像存在非常模糊，眼睛鼻子嘴巴遮挡至少其中一种或多种的情况；
             * 2: 一般的质量要求，图像存在偏亮，偏暗，模糊或一般模糊，眉毛遮挡，脸颊遮挡，下巴遮挡，至少其中三种的情况；
             * 3: 较高的质量要求，图像存在偏亮，偏暗，一般模糊，眉毛遮挡，脸颊遮挡，下巴遮挡，其中一到两种的情况；
             * 4: 很高的质量要求，各个维度均为最好或最多在某一维度上存在轻微问题；
             * 默认 0 若图片质量不满足要求，则返回结果中会提示图片质量检测不符要求。
             */
            req.setQualityControl(2L);
            // 返回的resp是一个CreatePersonResponse的实例，与请求对象对应
            CreatePersonResponse resp = client.CreatePerson(req);
            // 输出json格式的字符串回包
//            System.out.println(CreatePersonResponse.toJsonString(resp));
            return resp.getFaceId();
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            return null;
        }
    }
}