package com.liqiming.ibm.wmq.demo.config;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.spring.boot.MQConfigurationProperties;
import com.ibm.mq.spring.boot.MQConnectionFactoryCustomizer;
import com.ibm.mq.spring.boot.MQConnectionFactoryFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.JMSException;
import java.util.List;

@Configuration
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
            ObjectProvider<List<MQConnectionFactoryCustomizer>> factoryCustomizers, JmsProperties jmsProperties) {
        MQConnectionFactory wrappedConnectionFactory = new MQConnectionFactoryFactory(properties,
                factoryCustomizers.getIfAvailable()).createConnectionFactory(MQConnectionFactory.class);
//        MQConnectionFactory wrappedConnectionFactory = MQConnectionFactoryConfiguration.createConnectionFactory(properties, factoryCustomizers);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(wrappedConnectionFactory);
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

    @Bean
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

    @Bean
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
