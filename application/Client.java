package application;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

import framework.registry.LocateGlobalRegistry;
import framework.service.Bank;
import framework.service.Sorter;

/** Client program.
 *
 * Note: For the the client to retrieve the stub of the remote object, it needs to know: (1) what the name of the object is, (2) which machine hosts the remote object. */
public class Client {

	private static String SERVICE_NAME = "Sorter";
	private static String SERVICE_NAME1 = "Bank";
	private static String SERVICE_HOST = "localhost";

	public static void main(String[] args) throws Exception {

		// THREAD DU PREMIER CLIENT
		Thread thread = new Thread() {
			public void run() {
				try {
					client1();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		// THREAD DU DEUXIEME CLIENT
		thread.start();

		Thread thread2 = new Thread() {
			public void run() {
				try {
					client2();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		thread2.start();

		// THREAD DU TROISIEME CLIENT
		Thread thread3 = new Thread() {
			public void run() {
				try {
					client3();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		thread3.start();

	}

	public static void client1() throws RemoteException, NotBoundException {

		Registry registry = LocateGlobalRegistry.getGlobalRegistry(SERVICE_HOST);
		System.out.println("client1: retrieved registry");

		//  CLIENT (1) : UTILISATION DU SERVICE SORTER
		Sorter sorter = (Sorter) registry.lookup(SERVICE_NAME);

		List<String> list = Arrays.asList("7", "5", "9", "1", "8");
		System.out.println("client1: send " + list);

		list = sorter.sort(list);
		System.out.println("client1: received " + list);

		list = Arrays.asList("pluton", "venus", "saturne", "mars");
		System.out.println("client1: sending " + list);

		list = sorter.reverseSort(list);
		System.out.println("client1: received " + list);

		//  CLIENT (1) : UTILISATION DU SERVICE BANCAIRE
		Bank bank = (Bank) registry.lookup(SERVICE_NAME1);

		bank.createAccount("Mohamed");
		bank.depositWithdraw(12, "Mohamed");
		bank.depositWithdraw(45, "Mohamed");
		bank.depositWithdraw(-5, "Mohamed");
		bank.depositWithdraw(19, "Mohamed");
		System.out.println("client1 : My balance : " + bank.checkBalance("Mohamed"));
		System.out.println("client1: exiting");

	}

	public static void client2() throws RemoteException, NotBoundException {

		Registry registry = LocateGlobalRegistry.getGlobalRegistry(SERVICE_HOST);
		System.out.println("client2 : retrieved registry");

		//  CLIENT (2) : UTILISATION DU SERVICE BANCAIRE
		Bank bank = (Bank) registry.lookup(SERVICE_NAME1);

		bank.createAccount("Mohamed2");

		bank.depositWithdraw(20, "Mohamed2");
		bank.depositWithdraw(2000, "Mohamed2");
		bank.depositWithdraw(-50, "Mohamed2");
		bank.depositWithdraw(30, "Mohamed2");
		System.out.println("client2 : My balance : " + bank.checkBalance("Mohamed2"));

		//  CLIENT (2) : UTILISATION DU SERVICE SORTER

		Sorter sorter = (Sorter) registry.lookup(SERVICE_NAME);

		List<String> list = Arrays.asList("9", "1", "7", "5", "8");
		System.out.println("client2: send " + list);

		list = sorter.sort(list);
		System.out.println("client2: received " + list);

		list = Arrays.asList("pluton", "mars", "saturne", "venus");
		System.out.println("client2: sending " + list);

		list = sorter.reverseSort(list);
		System.out.println("client2: received " + list);

		System.out.println("client2 : exiting");

	}

	public static void client3() throws RemoteException, NotBoundException {

		Registry registry = LocateGlobalRegistry.getGlobalRegistry(SERVICE_HOST);
		System.out.println("client3 : retrieved registry");

		//  CLIENT (3) : UTILISATION ALTERNATIVE DU SERVICE BANCAIRE ET SORTER
		Bank bank = (Bank) registry.lookup(SERVICE_NAME1);

		bank.createAccount("Mohamed3");

		bank.depositWithdraw(78, "Mohamed3");
		bank.depositWithdraw(960, "Mohamed3");

		Sorter sorter = (Sorter) registry.lookup(SERVICE_NAME);

		List<String> list = Arrays.asList("9", "2", "17", "1", "8");
		System.out.println("client3: send " + list);

		list = sorter.sort(list);
		System.out.println("client3: received " + list);

		list = Arrays.asList("uranus", "mars", "jupiter", "neptune");
		System.out.println("client3: sending " + list);

		list = sorter.reverseSort(list);
		System.out.println("client3: received " + list);

		System.out.println("client3 : exiting");

		bank.depositWithdraw(17, "Mohamed3");
		bank.depositWithdraw(-25, "Mohamed3");
		System.out.println("client3 : My balance : " + bank.checkBalance("Mohamed3"));

		System.out.println("client3 : exiting");

	}
}
