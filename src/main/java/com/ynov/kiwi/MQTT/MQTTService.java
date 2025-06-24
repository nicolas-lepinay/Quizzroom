package com.ynov.kiwi.MQTT;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTService {

	private String broker;
	private String clientId;	
	private String user;
	private String password;
	
	private String incomingMessage;
	
	public MQTTService(String broker, String clientId, String user, String password) {
		this.broker = broker;
		this.clientId = clientId;
		this.user = user;
		this.password = password;
	}
	
	public MqttClient connectToMQTT() {
		try {
			MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(user);
			options.setPassword(password.toCharArray());
			client.connect(options);
			return client;
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	
	public void sendMessageInTopic(String topic, String message) throws MqttPersistenceException, MqttException {
		MqttClient client = connectToMQTT();
		client.publish(topic, new MqttMessage(message.getBytes()));
		client.disconnect();
		client.close();
	}
	
	public String receiveMessageFromTopic(String topic) throws MqttException {
		incomingMessage = "";
		MqttClient client = connectToMQTT();
		
		client.setCallback(new MqttCallback() {

            public void connectionLost(Throwable cause) {
                System.out.println("connectionLost: " + cause.getMessage());
            }

            public void messageArrived(String topic, MqttMessage message) {
                incomingMessage = new String(message.getPayload());
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("deliveryComplete---------" + token.isComplete());
            }

		});
		
		client.subscribe(topic);
		client.disconnect();
		client.close();
		return incomingMessage;
	}
	
}
