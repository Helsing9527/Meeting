package com.scetop.meeting.tencentapi;

import com.scetop.meeting.controller.tencentapi.CreatePerson;
import com.scetop.meeting.server.IUserServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
public class test {
    @Autowired
    private Environment env;

    @Autowired
    private IUserServer userServer;

    @Test
    public void test() {
        userServer.saveFaceId("asdfasdfasdf", 23);
    }

}
