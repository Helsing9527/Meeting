package com.scetop.meeting.face;
import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.testng.annotations.Test;
import java.util.HashMap;

public class FaceService {

  public static final  AipFace client=new AipFace("26237537","Mui6MglWSqVVwQImqWkfo0PO","4Z0RuBnok5VIMIuq9fQsHYbsBsfDr436");
    //人脸库注册
    public static String add(String image, String userId) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_info", "user's info");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("action_type", "REPLACE");

        String imageType = "BASE64";
        String groupId = "test";

        // 人脸注册
        JSONObject res = client.addUser(image, imageType, groupId, userId, options);
        System.out.println(res.toString(2));

        return res.toString();
    }
    //    人脸搜索
    @Test
    public static String serche(String image) {
        // 传入可选参数调用接口
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("match_threshold", "70");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
//        options.put("user_id", "233451");
        options.put("max_user_num", "3");
        String imageType = "BASE64";
        String groupIdList = "3,2";

        // 人脸搜索
        JSONObject res = client.search(image, imageType, groupIdList, options);
        System.out.println(res.toString(2));

        return res.toString();
    }
}
