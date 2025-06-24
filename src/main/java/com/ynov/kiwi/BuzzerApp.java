package com.ynov.kiwi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.MqttException;
import com.ynov.kiwi.MQTT.MQTTService;

public class BuzzerApp {
    private static final int MAX_BUZZERS = 15;
    private int buzzerCount = 0;
    private final List<BuzzerPanel> buzzerList = new ArrayList<>();

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Gestion des Buzzers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        JPanel buzzersPanel = new JPanel(new FlowLayout());
        JScrollPane scrollPane = new JScrollPane(buzzersPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton buzzButton = new JButton("Fait buzzer la sélection");
        buzzButton.addActionListener(e -> {
            List<BuzzerPanel> selectedBuzzers = buzzerList.stream()
                    .filter(panel -> panel.checkbox.isSelected())
                    .collect(Collectors.toList());
            selectedBuzzers.forEach(panel -> panel.buzzer.doClick());
        });

        JButton addButton = new JButton("Ajouter un buzzer");
        addButton.addActionListener(e -> {
            if (buzzerCount < MAX_BUZZERS) {
                buzzerCount++;
                String buzzerLabel = "Buzzer " + buzzerCount;
                String buzzerNumber = String.format("%4s", Integer.toBinaryString(buzzerCount)).replace(' ', '0');

                BuzzerPanel buzzerPanel = new BuzzerPanel(new JButton(buzzerLabel), new JCheckBox(buzzerLabel));
                buzzerPanel.initBuzzer(buzzerPanel.buzzer, buzzerNumber);

                buzzersPanel.add(buzzerPanel);
                buzzersPanel.revalidate();
                buzzersPanel.repaint();

                buzzerList.add(buzzerPanel);
            } else {
                JOptionPane.showMessageDialog(frame, "Maximum de 15 buzzers atteint !");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buzzButton);
        buttonPanel.add(addButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BuzzerApp().createAndShowGUI());
    }
}
