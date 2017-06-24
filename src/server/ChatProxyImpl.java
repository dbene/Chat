package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import interfaces.ChatProxy;
import interfaces.ChatServer;
import interfaces.ClientProxy;

public class ChatProxyImpl extends UnicastRemoteObject implements ChatProxy {

	private static final long serialVersionUID = 6199935514589812436L;

	private ClientProxy clientProxy;

	private ChatServer chatServer;
	private String username;

	public ChatProxyImpl(ClientProxy cp, ChatServer csi) throws RemoteException {
		super();

		this.clientProxy = cp;
		this.chatServer = csi;
		this.username = cp.getUserName();

	}

	@Override
	public void sendMessage(String message) throws RemoteException {
		this.chatServer.broadcastMessage(this.username, message);
	}

	public void receiveMessage(String user, String message) throws RemoteException {
		this.clientProxy.receiveMessage(user, message);
	}

	public void receiveUserList(String userlist) throws RemoteException {
		this.clientProxy.receiveUserList(userlist);
	}

	public ClientProxy getClientProxy() {
		return clientProxy;
	}

	public void setClientProxy(ClientProxy clientProxy) {
		this.clientProxy = clientProxy;
	}

	public ChatServer getChatServer() {
		return chatServer;
	}

	public void setChatServer(ChatServer chatServer) {
		this.chatServer = chatServer;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
