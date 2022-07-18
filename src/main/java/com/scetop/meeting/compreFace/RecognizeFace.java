package com.scetop.meeting.compreFace;

import com.alibaba.fastjson.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 根据上传 imgBase64 搜索用户，返回用户姓名
 */
@Component
public class RecognizeFace {

    @Autowired
    private Environment env;

    public String recognizeFace(String imgBase64) {
        try {
            HttpResponse<String> response = Unirest.post(env.getProperty("compreFace.compreFaceUrl") + ":8000/api/v1/recognition/recognize?limit=1")
                    .header("Content-Type", "application/json")
                    .header("x-api-key", env.getProperty("compreFace.x-api-key"))
                    .body("{\r\n  \"file\":\"" + imgBase64 + "\"\r\n}")
                    .asString();
            // 将返回字符串转为 json 对象
            JSONObject respJson = JSONObject.parseObject(response.getBody());
            // 获取 json 对象中的 subject 并返回
            return respJson.getJSONArray("result").getJSONObject(0).getJSONArray("subjects").getJSONObject(0).getString("subject");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
