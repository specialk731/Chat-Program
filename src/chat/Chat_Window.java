package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chat_Window {

	private JFrame frame, setupframe;
	private JPanel bottomPanel;
	private JTextField textBox;
	private JButton sendButton;
	private JList list;
	private JPanel topPanel;
	private JLabel hostLabel;
	private JTextField hostField;
	private JLabel portLabel;
	private JTextField portField;
	private JToggleButton allowHosting;
	private JTextArea chatBox;
	private JTextField setup_serverText;
	private JTextField setup_portText;
	private String Address, Port;

	/**
	 * Create the application.
	 */
	public Chat_Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//Setup Frame

		setupframe = new JFrame("Chat Program Setup");
		setupframe.setSize(250, 150);
		setupframe.setResizable(false);
		setupframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setupframe.getContentPane().setLayout(null);
		
		JLabel setup_serverLabel = new JLabel("Server:");
		setup_serverLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setup_serverLabel.setBounds(22, 14, 46, 14);
		setupframe.getContentPane().add(setup_serverLabel);
		
		setup_serverText = new JTextField();
		setup_serverText.setBounds(78, 11, 132, 20);
		setupframe.getContentPane().add(setup_serverText);
		setup_serverText.setColumns(15);
		setup_serverText.setText(Address);
		setup_serverText.requestFocus();
		setup_serverText.selectAll();
		
		JLabel setup_portLabel = new JLabel("Port:");
		setup_portLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setup_portLabel.setBounds(22, 42, 46, 14);
		setupframe.getContentPane().add(setup_portLabel);
		
		setup_portText = new JTextField();
		setup_portText.setColumns(15);
		setup_portText.setBounds(78, 39, 132, 20);
		setupframe.getContentPane().add(setup_portText);
		
		JButton setup_connectButton = new JButton("Connect");
		setup_connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(setup_serverText.getText().length() > 0 && setup_portText.getText().length() > 0){
					Address = setup_serverText.getText();
					Port = setup_portText.getText();
					setupframe.setVisible(false);
					frame.setVisible(true);
				}else
					JOptionPane.showMessageDialog(null, "Server and Port are Required");
			}
		});
		setup_connectButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		setup_connectButton.setBounds(22, 70, 89, 23);
		setupframe.getContentPane().add(setup_connectButton);
		
		JButton setup_serverButton = new JButton("New Server");
		setup_serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(setup_serverText.getText().length() > 0 && setup_portText.getText().length() > 0){
					Address = setup_serverText.getText();
					Port = setup_portText.getText();
					new Chat_Server(Address, Port).run();
					setupframe.setVisible(false);
					frame.setVisible(true);
				} else
					JOptionPane.showMessageDialog(null, "Server and Port are Required");
			}
		});
		setup_serverButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		setup_serverButton.setBounds(121, 70, 89, 23);
		setupframe.getContentPane().add(setup_serverButton);
		setupframe.setVisible(true);
		
		//Program Frame
		frame = new JFrame("Chat Program");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		chatBox = new JTextArea();
		chatBox.setBackground(Color.LIGHT_GRAY);
		chatBox.setEditable(false);
		frame.getContentPane().add(chatBox, BorderLayout.CENTER);
		
		topPanel = new JPanel();
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		hostLabel = new JLabel("Current Host:");
		hostLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		topPanel.add(hostLabel);
		
		hostField = new JTextField();
		hostField.setHorizontalAlignment(SwingConstants.CENTER);
		hostField.setEditable(false);
		hostField.setText("128.128.128.128");
		topPanel.add(hostField);
		hostField.setColumns(15);
		
		portLabel = new JLabel("Current Port:");
		portLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		topPanel.add(portLabel);
		
		portField = new JTextField();
		portField.setHorizontalAlignment(SwingConstants.CENTER);
		portField.setEditable(false);
		portField.setText("64000");
		topPanel.add(portField);
		portField.setColumns(5);
		
		allowHosting = new JToggleButton("Allow Hosting");
		topPanel.add(allowHosting);
		
		bottomPanel = new JPanel();
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		textBox = new JTextField();
		bottomPanel.add(textBox);
		textBox.setColumns(30);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(textBox.getText().length() > 0){
					Chat.WriteToServer(textBox.getText());
					textBox.setText("");
				}
			}
		});
		bottomPanel.add(sendButton);
		
		list = new JList();
		frame.getContentPane().add(list, BorderLayout.EAST);
		
		frame.setVisible(false);
	}
	
	public void writetochatBox(String s){
		if (chatBox.getText().length() > 0)
			chatBox.append("\n" + s);
		else
			chatBox.append(s);
			
	}

}
