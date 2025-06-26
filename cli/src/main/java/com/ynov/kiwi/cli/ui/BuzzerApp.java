package com.ynov.kiwi.cli.ui;

import com.ynov.kiwi.cli.buzzer.*;
import com.ynov.kiwi.cli.config.Config;
import com.ynov.kiwi.cli.mqtt.MQTTService;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class BuzzerApp {
    private final BuzzerManager manager = new BuzzerManager();
    private final JPanel buzzersPanel = new JPanel(new GridLayout(0, 1));
    private MQTTService mqttService;

    public void createAndShowGUI() throws MqttException {
        mqttService = new MQTTService();

        JFrame frame = new JFrame("Quizz Room");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(buzzersPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Ajouter un buzzer");
        addButton.addActionListener(e -> {
            BuzzerPanel panel = addBuzzer();
            buzzersPanel.add(panel);
            buzzersPanel.revalidate();
            buzzersPanel.repaint();
        });

        JButton multiBuzzBtn = new JButton("Faire buzzer la sélection");
        multiBuzzBtn.addActionListener(e -> buzzSelectionSimultaneously());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(multiBuzzBtn);
        buttonPanel.add(addButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private BuzzerPanel addBuzzer() {
        Buzzer buzzer = manager.addBuzzer();
        BuzzerPanel panel = new BuzzerPanel(buzzer);
        panel.buzzerButton.addActionListener(ev -> sendBuzz(buzzer));

        // Inscription du buzzer à la partie
        try {
            String signupTopic = Config.get("mqtt.topic.signup");
            mqttService.publish(signupTopic, String.valueOf(buzzer.getId()));
        } catch (MqttException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'inscription MQTT : " + e.getMessage());
        }
        return panel;
    }

    private void sendBuzz(Buzzer buzzer) {
        if (!buzzer.isEnabled()) {
            JOptionPane.showMessageDialog(null, "Ce buzzer est désactivé.");
            return;
        }
        String topic = Config.get("mqtt.topic.buzz");
        try {
            mqttService.publish(topic, String.valueOf(buzzer.getId()));
        } catch (MqttException e) {
            JOptionPane.showMessageDialog(null, "Erreur MQTT : " + e.getMessage());
        }
    }

    private void buzzSelectionSimultaneously() {
        List<BuzzerPanel> selected = getSelectedPanels();
        if (selected.isEmpty()) return;
        CountDownLatch readyLatch = new CountDownLatch(selected.size());
        CountDownLatch startLatch = new CountDownLatch(1);

        // Pour chaque buzzer sélectionné, un thread qui attend le signal de départ
        for (BuzzerPanel panel : selected) {
            new Thread(() -> {
                readyLatch.countDown();
                try { readyLatch.await(); } catch (InterruptedException ignored) {}
                try { startLatch.await(); } catch (InterruptedException ignored) {}
                sendBuzz(new Buzzer(panel.buzzerId));
            }).start();
        }
        // Quand tous sont prêts, GO
        try {
            readyLatch.await();
        } catch (InterruptedException ignored) {}
        startLatch.countDown();
    }

    private List<BuzzerPanel> getSelectedPanels() {
        return manager.getBuzzers().stream()
                .map(b -> (BuzzerPanel) buzzersPanel.getComponent(b.getId() - 1))
                .filter(BuzzerPanel::isSelected)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws MqttException {
        SwingUtilities.invokeLater(() -> {
            try { new BuzzerApp().createAndShowGUI(); }
            catch (MqttException e) { e.printStackTrace(); }
        });
    }
}
