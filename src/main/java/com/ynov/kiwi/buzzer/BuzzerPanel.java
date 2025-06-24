package com.ynov.kiwi.buzzer;

import javax.swing.*;
import java.awt.*;

public class BuzzerPanel extends JPanel {
	public final JButton buzzerButton;
	public final JCheckBox selectCheckBox;
	public final int buzzerId;

	public BuzzerPanel(Buzzer buzzer) {
		this.buzzerId = buzzer.getId();
		this.buzzerButton = new JButton("Buzzer " + buzzerId);
		this.selectCheckBox = new JCheckBox();
		setLayout(new FlowLayout());
		add(buzzerButton);
		add(selectCheckBox);
	}

	public boolean isSelected() {
		return selectCheckBox.isSelected();
	}
}
