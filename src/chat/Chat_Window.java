package chat;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;

public class Chat_Window {

	private JFrame frame, setupframe;
	private JPanel bottomPanel, topPanel;
	private JTextField textBox, hostField, portField, setup_serverText, setup_portText, setup_usernameText, txtYes,txtYes_1;
	private JButton sendButton;
	private JLabel hostLabel, portLabel,lblHostable,lblHost;
	private JTextArea chatBox;
	private String Address, Port, UserName;
	private Chat_Server server;
	private Chat_Client client;
	private JTable Connection_Table;
	private static DefaultTableModel model = new DefaultTableModel();
	private JScrollPane scroll;

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
		setupframe.setSize(270, 185);
		setupframe.setLocationRelativeTo(null);
		setupframe.setResizable(false);
		setupframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setupframe.getContentPane().setLayout(null);
		
		JLabel setup_serverLabel = new JLabel("Server:");
		setup_serverLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setup_serverLabel.setBounds(10, 11, 90, 14);
		setupframe.getContentPane().add(setup_serverLabel);
		
		setup_serverText = new JTextField();
		setup_serverText.setBounds(110, 11, 144, 20);
		setupframe.getContentPane().add(setup_serverText);
		setup_serverText.setColumns(15);
		setup_serverText.setText(Address);
		setup_serverText.requestFocus();
		setup_serverText.selectAll();
		
		JLabel setup_portLabel = new JLabel("Port:");
		setup_portLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setup_portLabel.setBounds(10, 39, 90, 14);
		setupframe.getContentPane().add(setup_portLabel);
		
		setup_portText = new JTextField();
		setup_portText.setColumns(15);
		setup_portText.setBounds(110, 39, 144, 20);
		setupframe.getContentPane().add(setup_portText);
		
		JRadioButton AllowHosting = new JRadioButton("Allow Hosting");
		AllowHosting.setHorizontalAlignment(SwingConstants.CENTER);
		AllowHosting.setBounds(10, 94, 244, 23);
		AllowHosting.setSelected(true);
		setupframe.getContentPane().add(AllowHosting);
		setupframe.setVisible(true);
		
		JButton setup_connectButton = new JButton("Connect");
		setup_connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(setup_serverText.getText().length() > 0 && setup_portText.getText().length() > 0){
					Address = setup_serverText.getText();
					hostField.setText(Address);
					Port = setup_portText.getText();
					portField.setText(Port);
					UserName = setup_usernameText.getText();
					if(!AllowHosting.isSelected())
						txtYes_1.setText("No");						
					setupframe.setVisible(false);
					frame.setVisible(true);
					try{
					client = new Chat_Client(Address, Port, UserName, AllowHosting.isSelected());
					client.start();
					} catch (Exception e) {
						setupframe.setVisible(true);
						frame.setVisible(false);
						JOptionPane.showMessageDialog(null, "Could not connect to host");
					}
				}else
					JOptionPane.showMessageDialog(null, "Server and Port are Required");
			}
		});
		setup_connectButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		setup_connectButton.setBounds(10, 124, 113, 23);
		setupframe.getContentPane().add(setup_connectButton);
		
		JButton setup_serverButton = new JButton("New Server");
		setup_serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(setup_serverText.getText().length() > 0 && setup_portText.getText().length() > 0 && AllowHosting.isSelected()){
					Address = setup_serverText.getText();
					hostField.setText(Address);
					Port = setup_portText.getText();
					portField.setText(Port);
					UserName = setup_usernameText.getText();
					txtYes.setText("Yes");
					server = new Chat_Server(Address, Port);
					server.start();
					System.out.println("Got past creating server");
					setupframe.setVisible(false);
					frame.setVisible(true);
					try {
						Thread.sleep(1000);
						client = new Chat_Client(Address, Port, UserName, AllowHosting.isSelected());
						client.start();
					} catch (Exception e2) {
						setupframe.setVisible(true);
						frame.setVisible(false);
						JOptionPane.showMessageDialog(null, "Could not connect to host");
					}
					
				} else{
					if(!AllowHosting.isSelected())
						JOptionPane.showMessageDialog(null, "In order to host you must Allow Hosting");
					else
						JOptionPane.showMessageDialog(null, "Server and Port are Required");
				}
			}
		});
		setup_serverButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		setup_serverButton.setBounds(141, 124, 113, 23);
		setupframe.getContentPane().add(setup_serverButton);
		
		JLabel setup_usernameLabel = new JLabel("Username:");
		setup_usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setup_usernameLabel.setBounds(10, 67, 90, 14);
		setupframe.getContentPane().add(setup_usernameLabel);
		
		setup_usernameText = new JTextField();
		setup_usernameText.setColumns(15);
		setup_usernameText.setBounds(110, 67, 144, 20);
		setupframe.getContentPane().add(setup_usernameText);
		
		
		//Program Frame
		frame = new JFrame("Chat Program");
		frame.setSize(800,600);
		frame.setLocationRelativeTo(null);
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
		topPanel.add(hostField);
		hostField.setColumns(15);
		
		portLabel = new JLabel("Current Port:");
		portLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		topPanel.add(portLabel);
		
		portField = new JTextField();
		portField.setHorizontalAlignment(SwingConstants.CENTER);
		portField.setEditable(false);
		topPanel.add(portField);
		portField.setColumns(5);
		
		lblHost = new JLabel("Host: ");
		topPanel.add(lblHost);
		
		txtYes = new JTextField();
		txtYes.setText("No");
		txtYes.setEditable(false);
		txtYes.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(txtYes);
		txtYes.setColumns(10);
		
		lblHostable = new JLabel("Hostable:");
		topPanel.add(lblHostable);
		
		txtYes_1 = new JTextField();
		txtYes_1.setEditable(false);
		txtYes_1.setText("Yes");
		txtYes_1.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(txtYes_1);
		txtYes_1.setColumns(10);
		
		bottomPanel = new JPanel();
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		textBox = new JTextField();
		textBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendButton.doClick();
			}
		});
		bottomPanel.add(textBox);
		textBox.setColumns(30);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(textBox.getText().length() > 0){
					client.send(textBox.getText());
					textBox.setText("");
				}
				//UpdateHost();
			}
		});
		bottomPanel.add(sendButton);
		
		Connection_Table = new JTable(model);
		Connection_Table.setEnabled(false);
		Connection_Table.setRowSelectionAllowed(false);
		scroll = new JScrollPane(Connection_Table);
		frame.getContentPane().add(scroll, BorderLayout.EAST);

		model.addColumn("Connections");
		
		DefaultTableCellRenderer DTCR = new DefaultTableCellRenderer();
		DTCR.setHorizontalAlignment(SwingConstants.CENTER);
		Connection_Table.getColumnModel().getColumn(0).setCellRenderer(DTCR);
		Connection_Table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
		
		model.addRow(new Object[]{"TEST"});
		
		AddToList("TEST2");
		RemoveFromList(0);
		
		frame.setVisible(false);
	}
	
	public void writetochatBox(String s){
		if (chatBox.getText().length() > 0)
			chatBox.append("\n" + s);
		else
			chatBox.append(s);
			
	}
	
	public String getAddress(){
		return Address;
	}
	
	public String getPort(){
		return Port;
	}
	
	public String getUserName(){
		return UserName;
	}
	
	private void UpdateHost(){
		Address = Chat_Client.GetHostAddress();
		Port = Chat_Client.GetHostPort();
		hostField.setText(Address);
		portField.setText(Port);
	}
	
	public static void AddToList(String s){
		model.addRow(new Object[]{s});
	}
	
	public static void RemoveFromList(int i){
		model.removeRow(i);
		
	}

}
