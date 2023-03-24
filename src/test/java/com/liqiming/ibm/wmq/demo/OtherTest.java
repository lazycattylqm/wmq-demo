package com.liqiming.ibm.wmq.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.jms.ConnectionFactory;

@SpringBootTest
@ActiveProfiles("test")
public class OtherTest {

    @MockBean
    ConnectionFactory connectionFactory;

    @Test
    public void other_test() {
        System.out.println("other test");
    }
}
