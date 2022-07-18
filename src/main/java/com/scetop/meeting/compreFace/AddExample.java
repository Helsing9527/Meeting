package com.scetop.meeting.compreFace;

import com.alibaba.fastjson.JSONObject;
import com.scetop.meeting.pojo.Staff;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AddExample {

    @Autowired
    private Environment env;

    public Boolean addExample(Staff staff) {
        try {
            HttpResponse<String> response = Unirest.post(env.getProperty("compreFace.compreFaceUrl") + ":8000/api/v1/recognition/faces?subject=" + staff.getId() + "_" +  staff.getUserName())
                    .header("Content-Type", "application/json")
                    .header("x-api-key", env.getProperty("compreFace.x-api-key"))
                    .body("{\r\n  \"file\": \""+ staff.getImgBase64() +"\"\r\n}")
                    .asString();
            String subject = JSONObject.parseObject(response.getBody()).getString("subject");
            if ((staff.getId() + "_" +  staff.getUserName()).equals(subject)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
