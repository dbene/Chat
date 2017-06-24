package server;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import interfaces.ChatProxy;
import interfaces.ChatServer;
import interfaces.ClientProxy;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

	private static final long serialVersionUID = -8162074978241390578L;

	private HashMap<String, ChatProxyImpl> clients;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	public static void main(String[] args) {
		ChatServer server = null;
		try {
			server = new ChatServerImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		System.out.println("Starting Server...");
		try {
			System.setProperty("java.rmi.server.hostname", "127.0.0.1");
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			Naming.rebind("ChatServer", server);
			System.out.println("Server \"ChatServer\" erfolgreich gestartet.");

		} catch (RemoteException ex) {
			System.out.println(ex.getMessage());
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		}
	}

	public ChatServerImpl() throws RemoteException {
		clients = new HashMap<String, ChatProxyImpl>();
	}

	@Override
	public ChatProxy subscribeUser(ClientProxy handle) throws RemoteException {
		ChatProxyImpl result = null;

		if (!clients.containsKey(handle.getUserName())) {
			result = new ChatProxyImpl(handle, this);
			clients.put(handle.getUserName(), result);
			broadcastMessage("Server", handle.getUserName() + " hat den Chat betreten.");

			String userlist = getUserList();
			for (ChatProxyImpl client : clients.values()) {
				client.receiveUserList(userlist);
			}
		} else {
			handle.receiveMessage("Server", "Der Name \"" + handle.getUserName() + "\" ist bereits vergeben.");
		}

		return result;
	}

	@Override
	public boolean unsubscribeUser(ClientProxy handle) throws RemoteException {
		boolean result = false;

		if (clients.containsKey(handle.getUserName())) {
			clients.remove(handle.getUserName());
			result = true;
			broadcastMessage("Server", handle.getUserName() + " hat den Chat verlassen.");

			String userlist = getUserList();
			for (ChatProxyImpl client : clients.values()) {
				client.receiveUserList(userlist);
			}
		}

		return result;
	}

	public boolean broadcastMessage(String user, String message) throws RemoteException {
		System.out.println(message);
		
		try(PrintWriter output = new PrintWriter(new FileWriter("chat.log",true))) {
		    output.printf("%s\r\n", "(" + sdf.format(new Date()) + ") " + user + ": " + message);
		} catch (Exception e) {}
		
		for (ChatProxyImpl client : clients.values()) {
			client.receiveMessage(user, message);
		}

		return true;
	}

	public String getUserList() {
		StringBuilder sb = new StringBuilder();

		for (String clientName : clients.keySet()) {
			sb.append(clientName + "\r\n");
		}

		return sb.toString();
	}

}
