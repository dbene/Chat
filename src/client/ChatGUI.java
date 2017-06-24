package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatGUI extends JPanel {
	private static final long serialVersionUID = 5136904998287065741L;

	private static ClientProxyImpl client;
	boolean connected = false;

	private JButton sumitButton;
	private JScrollPane spChat;
	private JTextArea chatArea;
	private JTextField inputField;
	private JScrollPane spUserList;
	private JTextArea userList;
	private JTextField txtIP;
	private JLabel lblIP;
	private JLabel lblName;
	private JTextField txtUsername;
	private JButton btnConnect;

	public ChatGUI() {
		// construct components
		sumitButton = new JButton("Sent");
		chatArea = new JTextArea(5, 5);
		inputField = new JTextField(5);
		userList = new JTextArea(5, 5);
		txtIP = new JTextField(5);
		lblIP = new JLabel("IP:");
		lblName = new JLabel("Name:");
		txtUsername = new JTextField(5);
		btnConnect = new JButton("Connect");

		spChat = new JScrollPane(chatArea);
		spUserList = new JScrollPane(userList);

		// set components properties
		chatArea.setEditable(false);
		userList.setEditable(false);
		txtIP.setToolTipText("IP des Servers");
		txtIP.setText("127.0.0.1");
		txtUsername.setToolTipText("Anzeigenamen des Benutzers");

		userList.setText("");
		txtIP.setEnabled(true);
		txtUsername.setEnabled(true);
		sumitButton.setEnabled(false);
		inputField.setEnabled(false);

		// adjust size and set layout
		setPreferredSize(new Dimension(516, 471));
		setLayout(null);

		// add components
		add(sumitButton);
		add(spChat);
		add(inputField);
		add(spUserList);
		add(txtIP);
		add(lblIP);
		add(lblName);
		add(txtUsername);
		add(btnConnect);

		// set component bounds (only needed by Absolute Positioning)
		sumitButton.setBounds(410, 440, 100, 25);
		spChat.setBounds(5, 35, 400, 400);
		inputField.setBounds(5, 440, 400, 25);
		spUserList.setBounds(410, 35, 100, 400);
		txtIP.setBounds(30, 5, 150, 25);
		lblIP.setBounds(5, 5, 25, 25);
		lblName.setBounds(185, 5, 45, 25);
		txtUsername.setBounds(230, 5, 175, 25);
		btnConnect.setBounds(410, 5, 100, 25);

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (connected) {
					connected = !client.disconnect();
				} else {
					if (!txtUsername.getText().equals("")) {
						connected = client.connectTo(txtIP.getText(), txtUsername.getText());
						if (!connected) {
							addText("Verbindungsaufbau zum Server schlug fehl!");
						}
					} else {
						addText("Benutzername darf nicht leer sein!");
					}
				}

				if (connected) {
					btnConnect.setText("Disconnect");

					txtIP.setEnabled(false);
					txtUsername.setEnabled(false);

					sumitButton.setEnabled(true);
					inputField.setEnabled(true);
				} else {
					btnConnect.setText("Connect");
					userList.setText("");

					txtIP.setEnabled(true);
					txtUsername.setEnabled(true);

					sumitButton.setEnabled(false);
					inputField.setEnabled(false);
				}
			}
		});

		ActionListener alSumit = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (connected) {
					if (!inputField.getText().equals("")) {
						client.sentMessage(inputField.getText());
						inputField.setText("");
					}
				} else {
					addText("ERROR: Not connected to server!");
				}

			}
		};

		sumitButton.addActionListener(alSumit);
		inputField.addActionListener(alSumit);

	}

	public static void main(String[] args) {

		try {

			ChatGUI gui = new ChatGUI();
			client = new ClientProxyImpl(gui);

			JFrame frame = new JFrame("Chat Client");
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(gui);
			frame.pack();
			frame.setVisible(true);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void addText(String msg) {
		this.chatArea.setText(this.chatArea.getText() + "\r\n" + msg);
	}

	public void setUserList(String userlist) {
		this.userList.setText(userlist);
	}
}
