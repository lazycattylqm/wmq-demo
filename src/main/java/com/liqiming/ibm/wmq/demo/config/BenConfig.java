package com.liqiming.ibm.wmq.demo.config;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.spring.boot.MQConfigurationProperties;
import com.ibm.mq.spring.boot.MQConnectionFactoryCustomizer;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.JMSException;
import java.util.List;

@Configuration
@Profile({"!test"})
public class BenConfig {
    private ObjectProvider<List<MQConnectionFactoryCustomizer>> factoryCustomizers;

    @Autowired
    public void setFactoryCustomizers(ObjectProvider<List<MQConnectionFactoryCustomizer>> factoryCustomizers) {
        this.factoryCustomizers = factoryCustomizers;
    }

    @Bean
    public MQConfigurationProperties properties() {
        return new MQConfigurationProperties();
    }

    @Bean
    public CachingConnectionFactory cachingJmsConnectionFactory(MQConfigurationProperties properties,
            ObjectProvider<List<MQConnectionFactoryCustomizer>> factoryCustomizers, JmsProperties jmsProperties)
            throws JMSException {

        final MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setChannel("DEV.ADMIN.SVRCONN");
        mqQueueConnectionFactory.setQueueManager("QM1");
        mqQueueConnectionFactory.setHostName("localhost");
        mqQueueConnectionFactory.setPort(1414);
        mqQueueConnectionFactory.setStringProperty(WMQConstants.PASSWORD, "passw0rd");
        mqQueueConnectionFactory.setStringProperty(WMQConstants.USERID, "admin");
        mqQueueConnectionFactory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
        mqQueueConnectionFactory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
//        MQConnectionFactory wrappedConnectionFactory = new MQConnectionFactoryFactory(properties,
//                factoryCustomizers.getIfAvailable()).createConnectionFactory(MQConnectionFactory.class);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(mqQueueConnectionFactory);
        connectionFactory.setCacheConsumers(true);
        connectionFactory.setCacheProducers(true);
        connectionFactory.setSessionCacheSize(1);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(CachingConnectionFactory cachingJmsConnectionFactory) {
        final JmsTemplate jmsTemplate = new JmsTemplate(cachingJmsConnectionFactory);
        return jmsTemplate;
    }

    //    @Bean
    public SimpleJmsListenerEndpoint jmsListenerEndpoint(
            CachingConnectionFactory cachingJmsConnectionFactory) {
        final SimpleJmsListenerEndpoint simpleJmsListenerEndpoint = new SimpleJmsListenerEndpoint();
        simpleJmsListenerEndpoint.setMessageListener(val -> {
            final String body;
            try {
                body = val.getBody(String.class);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
            System.out.println(body);
        });
        simpleJmsListenerEndpoint.setDestination("DEV.QUEUE.1");
        return simpleJmsListenerEndpoint;

    }

    //    @Bean
    public DefaultMessageListenerContainer defaultJmsListenerContainerFactory(
            CachingConnectionFactory cachingJmsConnectionFactory, SimpleJmsListenerEndpoint jmsListenerEndpoint) {
        final DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory =
                new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(cachingJmsConnectionFactory);
        final DefaultMessageListenerContainer listenerContainer =
                defaultJmsListenerContainerFactory.createListenerContainer(jmsListenerEndpoint);
        return listenerContainer;
    }

}
