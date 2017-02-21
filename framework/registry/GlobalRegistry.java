package framework.registry;

import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;
import framework.service.Service;

public class GlobalRegistry implements Iregistry {

	// stockage des services : String nom du nom service | Remote le service, integer 1 : réplique primaire
	private Map<String, ArrayList<Pair<Remote, Integer>>> services = new HashMap<String, ArrayList<Pair<Remote, Integer>>>();

	private static final int REGISTRY_PORT = 1099;

	// répartition circulation, ou par l'usage du CPU
	private BalancingMode _balancingMode;

	// index par service, pour distribué les répliques de manière circulaire
	private Map<String, Integer> indexRoundRobin = new HashMap<String, Integer>();

	// Utilisé pour ordonner le message par service. On garde le dernier index qui doit être communiqué
	private Map<String, Integer> indexFifo = new HashMap<String, Integer>();

	// Mode de réplication par service (ACTIVE, PASSIVE,SEMI_ACTIVE)
	private Map<String, ReplicationMode> replicationMode = new HashMap<String, ReplicationMode>();

	// Objets utilisés  pour implémenter le système de mutex
	private static Object lock = new Object();
	private static Object lock2 = new Object();
	private static Object lock3 = new Object();

	public GlobalRegistry(BalancingMode balancingMode) throws RemoteException {
		_balancingMode = balancingMode;

	}

	public static synchronized void main(String[] args) throws Exception {

		System.out.println("Global registry: running on host " + InetAddress.getLocalHost());

		LocateGlobalRegistry.createGlobalRegistry(REGISTRY_PORT, BalancingMode.balancingByCpu);
		System.out.println("Global registry: listening on port " + REGISTRY_PORT);

		GlobalRegistry.class.wait();

		System.out.println("Global registry: exiting (should not happen)");
	}

	public Remote lookup(String name) throws RemoteException, AccessException, NotBoundException {

		if (services.containsKey(name)) {

			if (replicationMode.get(name) == ReplicationMode.PASSIVE) {
				return getPrimaryReplicaByService(name);
			}

			if (_balancingMode == BalancingMode.balancingByCpu) {
				return balancingByCpu(name);
			} else {
				return roundRobin(name);
			}

		} else {
			System.out.println("Method lookup : this service do not exist" + name);
			throw new NotBoundException(name);
		}

	}

	private Remote balancingByCpu(String name) throws RemoteException {
		//bestRemote permet d'identifier  la réplique dont le taux d'utilisation CPU est le plus bas
		Pair<Integer, Double> bestRemote = new Pair<Integer, Double>(0, 0.0);// Integer: index | Double: cpu usage
		int i = 0;
		bestRemote = new Pair<Integer, Double>(0, ((Service) services.get(name).get(0).getKey()).chargerServer());
		for (Pair<Remote, Integer> r : services.get(name)) {

			if (((Service) r.getKey()).chargerServer() < bestRemote.getValue()) {
				bestRemote = new Pair<Integer, Double>(i, ((Service) r.getKey()).chargerServer());
			}

			i++;
		}

		System.out.println("BEST REMOTE of service " + name + " is remote with index " + bestRemote.getKey() + " cpu usage of " + bestRemote.getValue());

		return (Remote) services.get(name).get(bestRemote.getKey()).getKey();

	}

	private synchronized Remote roundRobin(String name) {

		int index = indexRoundRobin.get(name);
		if (index >= services.get(name).size() - 1) {
			indexRoundRobin.replace(name, index, 0);

		} else {
			indexRoundRobin.replace(name, index + 1);

		}

		System.out.println("roundRobin => return service " + name + " with current index : " + index + " || Next index: " + indexRoundRobin.get(name));
		return services.get(name).get(index).getKey();

	}

	public void bind(String name, final Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

		if (services.containsKey(name)) {

			services.get(name).add(new Pair<Remote, Integer>(obj, 0));
		}

		else {

			services.put(name, new ArrayList<Pair<Remote, Integer>>());
			services.get(name).add(new Pair<Remote, Integer>(obj, 1));// parametre 1 qui sera la réplique primaire
			indexRoundRobin.put(name, 0);
			indexFifo.put(name, 0);
			replicationMode.put(name, ReplicationMode.ACTIVE);// réplication par défaut
		}

	}

	public void rebind(String name, Remote obj) throws RemoteException, AccessException {
		services.remove(name);
		try {
			bind(name, obj);
		} catch (AlreadyBoundException e) {
			System.out.println("Method rebind : Object already bind");
			e.printStackTrace();
		}
	}

	public void unbind(String name) throws RemoteException, NotBoundException, AccessException {
		services.remove(name);
	}

	public String[] list() throws RemoteException, AccessException {
		String[] result = (String[]) services.keySet().toArray();
		return result;
	}

	public int getIndexFifo(String name) throws RemoteException {
		int currentIndex = 0;
		synchronized (lock3) {
			synchronized (lock2) {
				currentIndex = indexFifo.get(name);
				System.out.println("Attribution de l'id : (" + currentIndex + ") ,pour le service " + name);
				setIndexFifo(name);
				return currentIndex;
			}
		}
	}

	public void setIndexFifo(String name) throws RemoteException {
		synchronized (lock3) {
			synchronized (lock) {
				indexFifo.replace(name, indexFifo.get(name) + 1);

			}
		}
	}

	public ReplicationMode getReplicationMode(String name) throws RemoteException {
		return replicationMode.get(name);

	}

	public void setReplicationMode(String name, ReplicationMode rp) {
		replicationMode.replace(name, rp);
	}

	public Remote getPrimaryReplicaByService(String name) throws RemoteException {
		for (Pair<Remote, Integer> r : services.get(name)) {
			if (r.getValue() == 1) {
				return r.getKey();
			}
		}
		return null;
	}

	public ArrayList<Pair<Remote, Integer>> getServices(String name) throws RemoteException {

		return services.get(name);

	}

}
