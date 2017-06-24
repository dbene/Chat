package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

import interfaces.ChatProxy;
import interfaces.ChatServer;
import interfaces.ClientProxy;

public class ClientProxyImpl extends UnicastRemoteObject implements ClientProxy {

	private static final long serialVersionUID = 4721794609438359746L;
	private ChatGUI gui;
	private ChatServer server;
	private ChatProxy chat;
	private String username;

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	protected ClientProxyImpl(ChatGUI pGUI) throws RemoteException {
		super();
		this.gui = pGUI;
	}

	@Override
	public String getUserName() throws RemoteException {
		return this.username;
	}

	@Override
	public void receiveMessage(String username, String message) throws RemoteException {
		this.gui.addText("(" + sdf.format(new Date()) + ") " + username + ": " + message);
	}

	@Override
	public void receiveUserList(String usernames) {
		this.gui.setUserList(usernames);
	}

	public boolean sentMessage(String message) {
		boolean result = false;
		try {
			result = this.server.broadcastMessage(username, message);
		} catch (RemoteException e) {
			result = false;
		}
		return result;
	}

	public boolean connectTo(String ip, String name) {
		boolean result = false;
		try {
			this.username = name;

			this.server = (ChatServer) Naming.lookup("rmi://" + ip + "/ChatServer");
			this.chat = server.subscribeUser(this);
			if (this.chat != null) {
				result = true;
			}
		} catch (MalformedURLException e) {
			result = false;
		} catch (RemoteException e) {
			result = false;
		} catch (NotBoundException e) {
			result = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean disconnect() {
		boolean result = false;

		try {
			result = this.server.unsubscribeUser(this);
			this.server = null;

		} catch (RemoteException e) {
			result = false;
		}

		return result;
	}

}
