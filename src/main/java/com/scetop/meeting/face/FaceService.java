package com.scetop.meeting.face;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FaceService {
    //人脸库注册
    public static String add(String img, String user_id, String user_info) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            String imgParam = URLEncoder.encode(img, "UTF-8");
            Map<String, Object> map = new HashMap<>();
            map.put("image", img);////图片base64数据
            //活体检测
            map.put("liveness_control", "NORMAL");
            //搜索的组
            map.put("group_id", "test");
            //图片格式
            map.put("image_type", "BASE64");
            //图片质量要求
            map.put("quality_control", "LOW");
            //人物id
            map.put("user_id", user_id);
            //人物信息
            map.put("user_info", user_info);

            String param = GsonUtils.toJson(map);

            // 注意Param格式的编写，此处是最核心的内容，注意uid、user_info、group_id以及images的含义，详细信息看下图参数表，这里添加的图片数量可以自己权衡
            String accessToken = AuthService.getAuth();
            String result = HttpUtil.post(url, accessToken, "application/json",param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
