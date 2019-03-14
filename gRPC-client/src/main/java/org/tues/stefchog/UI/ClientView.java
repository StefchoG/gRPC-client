package org.tues.stefchog.UI;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.google.protobuf.Descriptors.DescriptorValidationException;

import io.grpc.internal.testing.TestClientStreamTracer;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ClientView {

	private JFrame frame;
	private JTextField textFieldName;
	private JTextField textFieldValue;

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
	    
	    JLabel lblNewLabel = new JLabel("Polyglot - a GUI client!");
	    lblNewLabel.setBounds(0, 0, 193, 29);
	    lblNewLabel.setForeground(new Color(255, 165, 0));
	    lblNewLabel.setBackground(new Color(0, 0, 0));
	    lblNewLabel.setFont(new Font("Arial", Font.BOLD, 18));
	    frame.getContentPane().add(lblNewLabel);
	    
	    JButton historyButton = new JButton("History?");
	    historyButton.setBounds(403, 5, 71, 23);
	    frame.getContentPane().add(historyButton);
	    
	    historyButton.addActionListener(new ActionListener() {
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
	    
	    JLabel labelKeyName = new JLabel("Key name");
	    labelKeyName.setBounds(21, 34, 87, 14);
	    frame.getContentPane().add(labelKeyName);
		
				JLabel labelKeyValue = new JLabel("Key value");
				labelKeyValue.setBounds(118, 34, 101, 14);
				frame.getContentPane().add(labelKeyValue);
		
		JButton addFormButton = new JButton("Add");
		addFormButton.setBounds(413, 40, 61, 23);
		frame.getContentPane().add(addFormButton);
		
		
		List<JTextField>textFieldNames = new ArrayList();
		List<JTextField>textFieldValues = new ArrayList();

		
		addFormButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Create a new line on field values
				System.out.println("Adding new item" + textFieldNames.size());
				JTextField name = new JTextField();
				JTextField value = new JTextField();
				
				//Create the 
				name.setBounds(10,textFieldNames.size()  * 30 + 55, 86, 20);
				name.setColumns(10);
				value.setBounds(117,textFieldNames.size()  * 30 + 55, 86, 20);
				value.setColumns(10);
				
				textFieldNames.add(name);
				textFieldValues.add(value);

				for(int i=0;i<textFieldNames.size();i++) {
					frame.getContentPane().add(textFieldNames.get(i));
					frame.getContentPane().add(textFieldValues.get(i));
					//Update the GUI
					frame.repaint();
				}
			}
		});
		
		

		
		JButton sendButton = new JButton("Send!");
		sendButton.setBounds(10, 315, 87, 23);
		sendButton.setVerticalAlignment(SwingConstants.BOTTOM);
		frame.getContentPane().add(sendButton);
		
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Client client = new Client();
				String resultMessage = "";
				try {
				    List<String> names = textFieldNames.stream().map(n -> n.getText()).collect(Collectors.toList());
				    List<String> values = textFieldValues.stream().map(v -> v.getText()).collect(Collectors.toList());

					resultMessage = client.execute(names , values);
					
				} catch (DescriptorValidationException e1) {
					// TODO Auto-generated catch block
					String errorMessage = "error Exception get message:" + e1.getMessage();
					JOptionPane.showMessageDialog(sendButton, errorMessage);
				}
				JOptionPane.showMessageDialog(sendButton,resultMessage);
			}
		});
		
		JButton protoButton = new JButton("Already have protobuf?");
		protoButton.setBounds(306, 316, 168, 23);
		protoButton.setForeground(new Color(0, 0, 0));
		protoButton.setBackground(new Color(255, 255, 255));
		frame.getContentPane().add(protoButton);
		
		
	}
}
