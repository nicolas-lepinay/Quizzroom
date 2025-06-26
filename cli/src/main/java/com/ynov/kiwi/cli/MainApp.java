package com.ynov.kiwi.cli;

import com.ynov.kiwi.cli.ui.BuzzerApp;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainApp {
    public static void main(String[] args) throws MqttException {
        BuzzerApp.main(args);
    }
}
