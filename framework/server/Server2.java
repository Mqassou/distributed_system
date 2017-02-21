package framework.server;

import java.net.InetAddress;
import java.rmi.server.UnicastRemoteObject;

import framework.registry.LocateGlobalRegistry;
import framework.service.Bank;
import framework.service.BankingServices;
import framework.service.SimpleSorter1;
import framework.service.Sorter;

/**
 * Server program.
 *
 * Note: After the main method exits, the JVM will still run. This is because the skeleton implements a non-daemon listening thread, which waits for incoming requests forever.
 *
 */
public class Server2 {

	//
	// CONSTANTS
	//
	private static final String SERVICE_NAME = "Sorter";
	private static final String SERVICE_NAME1 = "Bank";

	//
	// MAIN
	//
	public static void main(String[] args) throws Exception {

		// check the name of the local machine (two methods)
		System.out.println("Server2: running on host " + InetAddress.getLocalHost());
		System.out.println("Server2: hostname property " + System.getProperty("java.rmi.server.hostname"));

		// SORTER

		// instanciate the remote object 
		Sorter sorter = new SimpleSorter1(SERVICE_NAME);
		System.out.println("Server1: instanciated SimpleSorter");
		// create a skeleton and a stub for that remote object 
		Sorter stub = (Sorter) UnicastRemoteObject.exportObject(sorter, 0);
		System.out.println("Server1: generated skeleton and stub");

		// register the remote object's stub in the registry

		LocateGlobalRegistry.getGlobalRegistry().bind(SERVICE_NAME, stub);

		// BANK

		Bank bank = new BankingServices(SERVICE_NAME1);
		Bank stub2 = (Bank) UnicastRemoteObject.exportObject(bank, 0);
		LocateGlobalRegistry.getGlobalRegistry().bind(SERVICE_NAME1, stub2);
		System.out.println("Server2: registered remote object's stub");

		// main terminates here, but the JVM still runs because of the skeleton
		System.out.println("Server2: ready");

	}
}
