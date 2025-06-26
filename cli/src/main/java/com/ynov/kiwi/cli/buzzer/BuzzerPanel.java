package com.ynov.kiwi.cli.buzzer;

import javax.swing.*;
import java.awt.*;

public class BuzzerPanel extends JPanel {
	private final Buzzer buzzer;
	public final JButton buzzerButton;
	public final JCheckBox selectCheckBox;

	public BuzzerPanel(Buzzer buzzer) {
		this.buzzer = buzzer;
		this.buzzerButton = new JButton("Buzzer " + buzzer.getId());
		this.selectCheckBox = new JCheckBox();
		setLayout(new FlowLayout());
		add(buzzerButton);
		add(selectCheckBox);
	}

	public boolean isSelected() {
		return selectCheckBox.isSelected();
	}

	public void setBuzzerEnabled(boolean enabled) {
		buzzer.setEnabled(enabled);
		buzzerButton.setEnabled(enabled);
	}

	public Buzzer getBuzzer() {
		return buzzer;
	}
}
