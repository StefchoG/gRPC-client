package org.tues.stefchog.polyglot;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.TextField;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import java.awt.Color;

public class ClientView extends TestClient {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientView window = new ClientView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JTextArea textArea = new JTextArea();
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridheight = 6;
		gbc_textArea.gridwidth = 17;
		gbc_textArea.insets = new Insets(0, 0, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 1;
		gbc_textArea.gridy = 0;
		frame.getContentPane().add(textArea, gbc_textArea);
		
		String text = textArea.getText();
		
		JButton btnSend = new JButton("Send!");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				TestClient.main(text); //TODO: FIND A WAY TO SEND THE TEXT!
			}
		});
		
		JCheckBox chckbxAddInHistory = new JCheckBox("Add in history.");
		GridBagConstraints gbc_chckbxAddInHistory = new GridBagConstraints();
		gbc_chckbxAddInHistory.gridwidth = 5;
		gbc_chckbxAddInHistory.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAddInHistory.gridx = 7;
		gbc_chckbxAddInHistory.gridy = 6;
		frame.getContentPane().add(chckbxAddInHistory, gbc_chckbxAddInHistory);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBackground(new Color(200, 200, 200));
		GridBagConstraints gbc_scrollBar = new GridBagConstraints();
		gbc_scrollBar.fill = GridBagConstraints.BOTH;
		gbc_scrollBar.gridheight = 8;
		gbc_scrollBar.insets = new Insets(0, 0, 5, 5);
		gbc_scrollBar.gridx = 18;
		gbc_scrollBar.gridy = 0;
		frame.getContentPane().add(scrollBar, gbc_scrollBar);
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridwidth = 4;
		gbc_btnSend.fill = GridBagConstraints.VERTICAL;
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 7;
		gbc_btnSend.gridy = 7;
		frame.getContentPane().add(btnSend, gbc_btnSend);
	}

}
