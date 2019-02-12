package org.tues.stefchog.UI;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class ClientView {

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
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 500, 399);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(false);
		frame.setVisible(true);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
	    frame.getContentPane().setLayout(null);
	    
	    JScrollBar scrollBar = new JScrollBar();
	    scrollBar.setBounds(467, 0, 17, 360);
	    frame.getContentPane().add(scrollBar);
	    
	    JTextArea txtrSyntax = new JTextArea();
	    txtrSyntax.setBounds(10, 45, 447, 269);
	    txtrSyntax.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
	    
	    
	    
	    frame.getContentPane().add(txtrSyntax);
		
		JButton btnNewButton = new JButton("Send!");
		btnNewButton.setBounds(10, 325, 92, 23);
		btnNewButton.setVerticalAlignment(SwingConstants.BOTTOM);
		frame.getContentPane().add(btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(btnNewButton,txtrSyntax.getText());
			}
		});
		
		JLabel lblNewLabel = new JLabel("Polyglot - a GUI client!");
		lblNewLabel.setForeground(new Color(255, 165, 0));
		lblNewLabel.setBackground(new Color(0, 0, 0));
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 18));
		lblNewLabel.setBounds(10, 0, 193, 34);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton_1 = new JButton("Already have protobuf?");
		btnNewButton_1.setForeground(new Color(0, 0, 0));
		btnNewButton_1.setBackground(new Color(255, 255, 255));
		btnNewButton_1.setBounds(311, 326, 147, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("History?");
		btnNewButton_2.setBounds(368, 8, 89, 23);
		frame.getContentPane().add(btnNewButton_2);
		
		btnNewButton_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					HistoryDialog dialog = new HistoryDialog();
				    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
				    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				    dialog.setVisible(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}		});
	}
}
