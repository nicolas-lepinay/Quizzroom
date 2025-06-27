package com.ynov.kiwi.api.service;

import com.ynov.kiwi.api.config.MqttConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class MqttService {
    private final PlayerService playerService;
    private final MqttConfig mqttConfig;
    private MqttClient client;

    public MqttService(@Lazy PlayerService playerService, MqttConfig mqttConfig) {
        this.playerService = playerService;
        this.mqttConfig = mqttConfig;
    }

    @PostConstruct
    public void init() throws Exception {
        String broker = mqttConfig.getBrokerUrl();
        String username = mqttConfig.getUsername();
        String password = mqttConfig.getPassword();
        String clientId = "API-" + System.currentTimeMillis();

        client = new MqttClient(broker, clientId, new MemoryPersistence());
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName(username);
        opts.setPassword(password.toCharArray());
        client.connect(opts);

        // Abonnement inscription buzzer
        client.subscribe(mqttConfig.getSignupTopic(), (topic, message) -> {
            int id = Integer.parseInt(new String(message.getPayload()));
            boolean ok = playerService.addPlayer(id);
            System.out.println(ok ? "[MQTT] Joueur #" + id + " inscrit." : "[MQTT] Inscription refusée (déjà inscrit ou partie démarrée) pour id=" + id);
        });

        // Abonnement buzz
        client.subscribe(mqttConfig.getBuzzTopic(), (topic, message) -> {
            int id = Integer.parseInt(new String(message.getPayload()));
            boolean ok = playerService.processBuzz(id);
            System.out.println(ok ? "[MQTT] Joueur #" + id + " a la main !" : "[MQTT] Buzz ignoré.");
        });
    }

    // Remplacé par du SSE (de l'API --> vers CLI Java Swing)
    /*
    public void publishEnable(int playerId) {
        try {
            client.publish(mqttConfig.getEnableTopic(), new MqttMessage(String.valueOf(playerId).getBytes()));
            System.out.println("[MQTT] ACTIVATION du buzzer #" + playerId);
        } catch (Exception e) {
            System.err.println("Erreur publish ENABLE: " + e.getMessage());
        }
    }

    public void publishDisable(int playerId) {
        try {
            client.publish(mqttConfig.getDisableTopic(), new MqttMessage(String.valueOf(playerId).getBytes()));
            System.out.println("[MQTT] DÉSACTIVATION du buzzer #" + playerId);
        } catch (Exception e) {
            System.err.println("Erreur publish DISABLE: " + e.getMessage());
        }
    }
    */
}
