package framework.registry;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LocateGlobalRegistry {

	public static final String HOST = "localhost";
	public static final String GLOBAL_REGISTRY = "GlobalRegistry";

	public static Registry getGlobalRegistry(String host) throws RemoteException, NotBoundException {
		Registry r = LocateRegistry.getRegistry(host);
		return (Registry) r.lookup(GLOBAL_REGISTRY);
	}

	public static Registry getGlobalRegistry() throws RemoteException, NotBoundException {
		return getGlobalRegistry(HOST);
	}

	public static Registry createGlobalRegistry(int port, BalancingMode mode) throws RemoteException, AlreadyBoundException, NotBoundException {
		Registry r = LocateRegistry.createRegistry(port);
		Registry stub = (Registry) UnicastRemoteObject.exportObject(new GlobalRegistry(mode), 0);
		r.bind(GLOBAL_REGISTRY, stub);
		return stub;
	}
}
