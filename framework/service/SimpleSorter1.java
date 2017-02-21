package framework.service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

import framework.registry.Iregistry;
import framework.registry.LocateGlobalRegistry;

/**
 * A simple implementation of the <@link Sorter> using methods of class <code>Collections</code>. For test purposes, the <code>toString()</code> method displays the name of the current thread.
 *
 * Note: methods <code>sort</code> and <code>reverseSort</code> do not throw <code>RemoteException</code>. This shows that this exception is not thrown by the server code, but rather by the RMI runtime when a communication failure is detected in the object's stub, on the client side.
 *
 */
public class SimpleSorter1 implements Sorter {

	private Iregistry _registry;
	private String _serviceName;

	public SimpleSorter1(String serviceName) throws RemoteException, NotBoundException {
		_registry = (Iregistry) LocateGlobalRegistry.getGlobalRegistry();
		_serviceName = serviceName;

	}

	public List<String> sort(List<String> list) throws AccessException, RemoteException, NotBoundException {

		return ((Sorter) _registry.lookup(_serviceName)).sortReplica(list);

	}

	public List<String> reverseSort(List<String> list) throws AccessException, RemoteException, NotBoundException {

		return ((Sorter) _registry.lookup(_serviceName)).reverseSortReplica(list);
	}

	public List<String> sortReplica(List<String> list) throws RemoteException {

		System.out.println(this + ": receveid " + list);

		Collections.sort(list);

		System.out.println(this + ": returning " + list);
		return list;

	}

	public List<String> reverseSortReplica(List<String> list) throws RemoteException {

		System.out.println(this + ": receveid " + list);

		Collections.sort(list);
		Collections.reverse(list);

		System.out.println(this + ": returning " + list);
		return list;
	}

	public Sorter getReplica() throws RemoteException, NotBoundException {
		return (Sorter) _registry.lookup(_serviceName);

	}

	@Override
	public String toString() {
		return "SimpleSorter1 " + Thread.currentThread();
	}

	public double chargerServer() throws RemoteException {
		OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		return ((com.sun.management.OperatingSystemMXBean) os).getProcessCpuLoad();
	}

	public String getServiceName() throws RemoteException {

		return _serviceName;
	}

}
