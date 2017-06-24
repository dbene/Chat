package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientProxy extends Remote {
	public String getUserName() throws RemoteException;

	public void receiveMessage(String username, String message) throws RemoteException;

	public void receiveUserList(String usernames) throws RemoteException;
}