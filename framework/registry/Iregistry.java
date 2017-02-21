package framework.registry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javafx.util.Pair;

public interface Iregistry extends Registry {

	public Remote getPrimaryReplicaByService(String name) throws RemoteException;

	public ReplicationMode getReplicationMode(String name) throws RemoteException;

	public ArrayList<Pair<Remote, Integer>> getServices(String name) throws RemoteException;

	public int getIndexFifo(String name) throws RemoteException;

	public void setIndexFifo(String name) throws RemoteException;

}
