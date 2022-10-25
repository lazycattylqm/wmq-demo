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
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

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

}
