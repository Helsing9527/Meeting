package com.scetop.meeting.compreFace;

import com.alibaba.fastjson.JSONObject;
import com.scetop.meeting.pojo.Staff;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 根据 姓名 删除人员数据
 */

@Component
public class DeleteExample {

    @Autowired
    private Environment env;

    public Boolean deleteExample(Staff staff) {
        try {
            HttpResponse<String> response = Unirest.delete(env.getProperty("compreFace.compreFaceUrl") + ":8000/api/v1/recognition/faces?subject=" + staff.getId() + "_" +  staff.getUserName())
                    .header("x-api-key", env.getProperty("compreFace.x-api-key"))
                    .asString();
            String deleted = JSONObject.parseObject(response.getBody()).getString("deleted");
            if ("1".equals(deleted)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
