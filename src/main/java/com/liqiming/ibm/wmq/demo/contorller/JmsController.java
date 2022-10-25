package com.liqiming.ibm.wmq.demo.contorller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class JmsController {

    private JmsTemplate jmsTemplate;

    public JmsController(@Autowired JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        try {
            jmsTemplate.convertAndSend("DEV.QUEUE.1", message);
            return "success";
        } catch (JmsException e) {
            e.printStackTrace();
            return "failed";
        }
    }

    @GetMapping("/receive")
    public String readMessage() {
        try {
            return Objects.requireNonNull(jmsTemplate.receiveAndConvert("DEV.QUEUE.1"))
                    .toString();
        } catch (JmsException e) {
            e.printStackTrace();
            return "failed";
        }
    }


    @JmsListener(destination = "DEV.QUEUE.1")
    public void listener(Message<String> message) {
        System.out.println("listener");
        System.out.println(message.getPayload());
    }
}
