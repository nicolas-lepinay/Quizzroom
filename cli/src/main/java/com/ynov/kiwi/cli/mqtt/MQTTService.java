package com.ynov.kiwi.cli.mqtt;

import com.ynov.kiwi.cli.config.Config;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTService {
	private final MqttClient client;

	public MQTTService() throws MqttException {
		String broker = Config.get("mqtt.broker");
		String username = Config.get("mqtt.username");
		String password = Config.get("mqtt.password");
		String clientId = "CLI_BuzzerApp-" + System.currentTimeMillis();

		client = new MqttClient(broker, clientId, new MemoryPersistence());
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(username);
		options.setPassword(password.toCharArray());
		client.connect(options);
	}

	public void publish(String topic, String message) throws MqttException {
		client.publish(topic, new MqttMessage(message.getBytes()));
	}

	public void subscribe(String topic, IMqttMessageListener listener) throws MqttException {
		client.subscribe(topic, listener);
	}

	public void disconnect() throws MqttException {
		client.disconnect();
		client.close();
	}
}
