package com.ynov.kiwi.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker}")
    private String brokerUrl;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.topic.signup}")
    private String signupTopic;

    @Value("${mqtt.topic.buzz}")
    private String buzzTopic;

    @Value("${mqtt.topic.enable}")
    private String enableTopic;

    @Value("${mqtt.topic.disable}")
    private String disableTopic;

    public String getBrokerUrl() { return brokerUrl; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSignupTopic() { return signupTopic; }
    public String getBuzzTopic() { return buzzTopic; }
    public String getEnableTopic() { return enableTopic; }
    public String getDisableTopic() { return disableTopic; }

}
