package com.scetop.meeting.tencentapi;

import com.scetop.meeting.server.IUserServer;
import com.scetop.meeting.tencentapi.face.VerifyFace;
import com.scetop.meeting.tencentapi.person.GetPersonList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.List;

@SpringBootTest
public class test {
    @Autowired
    private Environment env;

    @Autowired
    private IUserServer userServer;

    @Autowired
    private GetPersonList getPersonList;

    @Autowired
    private VerifyFace verifyFace;

    @Test
    public void test() {
        List<String> personList = getPersonList.getPersonList();
        for (String s : personList) {
//            verifyFace.verifyFace()
            System.out.println(s);
        }

    }

}
