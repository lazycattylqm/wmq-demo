package com.liqiming.ibm.wmq.demo;


import com.liqiming.ibm.wmq.demo.contorller.JmsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

	@Autowired
	private JmsController controller;




	@Test
	void contextLoads() {
		controller.sendMessage("yes");
	}

}
