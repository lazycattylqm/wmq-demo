package com.liqiming.ibm.wmq.demo;


import com.liqiming.ibm.wmq.demo.contorller.JmsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
//@Testcontainers
public class DemoApplicationTests {

//	@Container
//	public static GenericContainer<?> activeMqContainer =
//			new GenericContainer<>(DockerImageName.parse("rmohr/activemq:5.14.3")).withExposedPorts(61616);

/*	@BeforeClass
	public static void beforeAll() {
		activeMqContainer =
		System.out.println("beforeAll");
	}*/


	@Autowired
	private JmsController controller;


	@Test
	void contextLoads() {
		controller.sendMessage("yes");
	}

}
