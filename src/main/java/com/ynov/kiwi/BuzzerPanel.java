package com.ynov.kiwi;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.ynov.kiwi.MQTT.MQTTService;

public class BuzzerPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final JButton buzzer;
	public final JCheckBox checkbox;
	
	public BuzzerPanel(JButton buzzer, JCheckBox checkbox) {
		this.buzzer = buzzer;
		this.checkbox = checkbox;
		add(this.buzzer);
		add(this.checkbox);
	}
	
	public void initBuzzer(JButton buzzer, String buzzerId) {
		buzzer.setName(buzzerId);
        Thread thread = new Thread(() -> buzzer.addActionListener((ActionEvent e) -> {
            MQTTService mqttService = new MQTTService(
                    "ssl://7215524ffd4b4705b058023e9580609c.s1.eu.hivemq.cloud:8883",
                    buzzer.getText(),
                    "hivemq.webclient.1750681089957",
                    "&:1T<S,2b0acdhe3IAQR"
            );
            mqttService.connectToMQTT();
            try {
                mqttService.sendMessageInTopic("test", buzzer.getName());
            } catch (MqttException ex) {
                ex.printStackTrace();
            }
            System.out.println(buzzer.getName());
        }));
        thread.start();
    }	
	
}
