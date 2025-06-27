package com.ynov.kiwi.cli.ui;

import com.ynov.kiwi.cli.buzzer.*;
import com.ynov.kiwi.cli.config.Config;
import com.ynov.kiwi.cli.mqtt.MQTTService;
import com.ynov.kiwi.cli.sse.SseClient;
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
    private SseClient sseClient;

    public void createAndShowGUI() throws MqttException {
        mqttService = new MQTTService();
        sseClient = new SseClient(Config.get("sse.url"));

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

        sseClient.setOnEnable(id -> {
            manager.getBuzzerById(id).ifPresent(buzzer -> {
                buzzer.setEnabled(true);
                SwingUtilities.invokeLater(() -> {
                    BuzzerPanel panel = getPanelByBuzzerId(id);
                    if (panel != null) panel.setBuzzerEnabled(true);
                });
            });
        });
        sseClient.setOnDisable(id -> {
            manager.getBuzzerById(id).ifPresent(buzzer -> {
                buzzer.setEnabled(false);
                SwingUtilities.invokeLater(() -> {
                    BuzzerPanel panel = getPanelByBuzzerId(id);
                    if (panel != null) panel.setBuzzerEnabled(false);
                });
            });
        });
        sseClient.setOnReset(() -> {
            System.out.println("[SSE] Event reset reçu, suppression de tous les buzzers !");
            manager.clear(); // Vide ta liste de buzzers
            SwingUtilities.invokeLater(() -> {
                buzzersPanel.removeAll();
                buzzersPanel.revalidate();
                buzzersPanel.repaint();
            });
        });

        sseClient.start();

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (sseClient != null) {
                    sseClient.stop();
                    System.out.println("SSE client stopped.");
                }
                System.exit(0);
            }
        });
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
                sendBuzz(new Buzzer(panel.getBuzzer().getId()));
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

    // Remplacé par SSE
    /*
    private void subscribeToEnableDisable() {
        try {
            String enableTopic = Config.get("mqtt.topic.enable");
            String disableTopic = Config.get("mqtt.topic.disable");

            mqttService.subscribe(enableTopic, (topic, msg) -> {
                int id = Integer.parseInt(new String(msg.getPayload()));
                // Mettre à jour le buzzer concerné (enabled = true)
                manager.getBuzzerById(id).ifPresent(buzzer -> {
                    // Met à jour l'état interne
                    buzzer.setEnabled(true);
                    // Met à jour l'affichage Swing (sur l'EDT)
                    SwingUtilities.invokeLater(() -> {
                        BuzzerPanel panel = getPanelByBuzzerId(id);
                        if (panel != null) panel.setBuzzerEnabled(true);
                    });
                });
            });

            mqttService.subscribe(disableTopic, (topic, msg) -> {
                int id = Integer.parseInt(new String(msg.getPayload()));
                // Mettre à jour le buzzer concerné (enabled = false)
                manager.getBuzzerById(id).ifPresent(buzzer -> {
                    buzzer.setEnabled(false);
                    SwingUtilities.invokeLater(() -> {
                        BuzzerPanel panel = getPanelByBuzzerId(id);
                        if (panel != null) panel.setBuzzerEnabled(false);
                    });
                });
            });
        } catch (MqttException e) {
            JOptionPane.showMessageDialog(null, "Erreur abonnement MQTT : " + e.getMessage());
        }
    }
    */

    private BuzzerPanel getPanelByBuzzerId(int id) {
        for (Component comp : buzzersPanel.getComponents()) {
            if (comp instanceof BuzzerPanel panel && panel.getBuzzer().getId() == id) {
                return panel;
            }
        }
        return null;
    }

    public static void main(String[] args) throws MqttException {
        SwingUtilities.invokeLater(() -> {
            try { new BuzzerApp().createAndShowGUI(); }
            catch (MqttException e) { e.printStackTrace(); }
        });
    }
}
