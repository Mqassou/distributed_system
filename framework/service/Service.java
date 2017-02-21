package framework.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {

	public double chargerServer() throws RemoteException;

	public String getServiceName() throws RemoteException;
}
