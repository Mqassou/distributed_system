package framework.service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.util.Pair;
import framework.registry.Iregistry;
import framework.registry.LocateGlobalRegistry;
import framework.registry.ReplicationMode;

public class BankingServices implements Bank {

	private Iregistry _registry;
	private String _serviceName;
	private Map<String, Integer> accounts;
	private int idTransaction = 0;// id permettant d'ordonner les transactions
	private List<BankTransaction> transactions;// Liste les transactions reçues en attente, pour pouvoir les ordonner
	private static Object lock = new Object();
	private static Object lock2 = new Object();
	private static Object lock3 = new Object();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public BankingServices(String serviceName) throws RemoteException, NotBoundException {
		_registry = (Iregistry) LocateGlobalRegistry.getGlobalRegistry();
		_serviceName = serviceName;
		accounts = new HashMap<String, Integer>();
		transactions = new ArrayList<BankTransaction>();

		/*
		 SI LE MODE DE REPLCICATION EST PASSIVE , ON DELENCHE LA TACHE DE MISE A JOUR PERIODIQUE
		 POUR LES REPLIQUES REPIMAIRES
		 */
		if (_registry.getReplicationMode(_serviceName) == ReplicationMode.PASSIVE) {
			scheduler.scheduleAtFixedRate(periodicUpdate(), 5, 5, TimeUnit.SECONDS);

		}

	}

	public int checkBalance(String accountName) throws RemoteException, NotBoundException {

		// SI LE MODE DE REPLICATION EST PASSIVE OU SEMI ACTIVE, ON RETOURNE LA REPLIQUE PRIMAIRE
		if (_registry.getReplicationMode(_serviceName) != ReplicationMode.ACTIVE) {
			return ((Bank) _registry.getPrimaryReplicaByService(_serviceName)).checkBalanceReplica(accountName);
		}
		// SI LE MODE DE REPLICATION EST ACTIVE ON RETOURNE LA REPLIQUE EN FONCTION DE LA REPARTION DE CHARGE
		return ((Bank) _registry.lookup(_serviceName)).checkBalanceReplica(accountName);
	}

	public int checkBalanceReplica(String accountName) throws RemoteException {

		if (accounts.containsKey(accountName)) {
			return accounts.get(accountName);
		} else {
			System.out.println("The customer " + accountName + " is not register");
			return 0;
		}

	}

	public synchronized void createAccount(String accountName) throws RemoteException, NotBoundException {

		BankTransaction bt = new BankTransaction(accountName, _registry.getIndexFifo(_serviceName), BankTransaction.TransactionType.createAccount);

		if (_registry.getReplicationMode(_serviceName) != ReplicationMode.PASSIVE) {
			for (Pair<Remote, Integer> r : _registry.getServices(_serviceName)) {
				((Bank) r.getKey()).notifyReplicas(bt);
			}

			if (scheduler.isShutdown() == false) {// ON STOP LA TACHE PERIODIQUE
				scheduler.shutdown();
			}

		} else {//MODE REPLICATION PASSIVE
			notifyReplicas(bt);// on met à jour sur la réplique primaire
			if (scheduler.isShutdown() == true) {// ON LANCE LA TACHE PERIODIQUE
				scheduler.scheduleAtFixedRate(periodicUpdate(), 5, 5, TimeUnit.SECONDS);
			}

		}

	}

	public void createAccountReplica(String accountName) throws RemoteException {
		System.out.println("createAccount : " + accountName);
		accounts.put(accountName, 0);
		idTransaction++;

	}

	public synchronized void depositWithdraw(int money, String accountName) throws RemoteException, NotBoundException {

		BankTransaction bt = new BankTransaction(accountName, money, _registry.getIndexFifo(_serviceName), BankTransaction.TransactionType.depositWithdraw);
		if (_registry.getReplicationMode(_serviceName) != ReplicationMode.PASSIVE) {
			for (Pair<Remote, Integer> r : _registry.getServices(_serviceName)) {
				((Bank) r.getKey()).notifyReplicas(bt);
			}

			if (scheduler.isShutdown() == false) {// ON STOP LA TACHE PERIODIQUE
				scheduler.shutdown();
			}

		} else {//MODE REPLICATION PASSIVE
			notifyReplicas(bt);// on met à jour sur la réplique primaire
			if (scheduler.isShutdown() == true) {// ON LANCE LA TACHE PERIODIQUE
				scheduler.scheduleAtFixedRate(periodicUpdate(), 5, 5, TimeUnit.SECONDS);
			}
		}

	}

	public void depositWithdrawReplica(int money, String accountName) throws RemoteException {
		int newMoney = accounts.get(accountName);
		newMoney = newMoney + money;
		accounts.replace(accountName, newMoney);
		idTransaction++;
	}

	public void notifyReplicas(BankTransaction bt) throws RemoteException {

		if (idTransaction == bt.get_idTransaction()) { // si l'id du message correspond à l'index local
			System.out.println(bt.toString());
			if (bt.get_typeTransaction() == BankTransaction.TransactionType.depositWithdraw) {

				depositWithdrawReplica(bt.get_amountTransaction(), bt.get_accountName());

			} else if (bt.get_typeTransaction() == BankTransaction.TransactionType.createAccount) {

				createAccountReplica(bt.get_accountName());

			}

		} else {
			synchronized (lock3) {
				synchronized (lock) {
					transactions.add(bt);// si l'id ne correspond pas on l'ajoute dans la file d'attente
				}
			}

		}
		updateTransactionsQueue();// on vérifie les messages dans la file d'attente pour pouvoir les traiter
	}

	public void updateTransactionsQueue() throws RemoteException {

		synchronized (lock3) {
			synchronized (lock2) {
				for (BankTransaction bt : transactions) {
					if (bt.get_idTransaction() == idTransaction) { // si l'id correspond on traite le message
						notifyReplicas(bt);

					}

				}

			}

		}

	}

	//Methode pour mettre à jour les repliques secondaires dans le mode de réplication passive
	public void updateSecondaryReplicas(Map<String, Integer> accountsToUpdate) throws RemoteException {

		for (String key : accountsToUpdate.keySet()) {
			if (accounts.containsKey(key)) {
				accounts.replace(key, accountsToUpdate.get(key));
			} else {
				accounts.put(key, accountsToUpdate.get(key));
			}
			System.out.println("mise à jour en cours du compte : " + key + " || Solde =" + accounts.get(key));

		}
	}

	// tâche périodique pour mettre à jour les répliques secondaires
	public Runnable periodicUpdate() throws RemoteException {

		return new Runnable() {
			public void run() {
				try {
					for (Pair<Remote, Integer> r : _registry.getServices(_serviceName)) {
						if (r.getValue() == 0) {
							// La réplique primaire fournit les comptes à jour aux répliques secondaires
							((Bank) r.getKey()).updateSecondaryReplicas(((Bank) _registry.getPrimaryReplicaByService(_serviceName)).getAccounts());

						}
					}
					System.out.println("mise à jour périodique terminée");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	public double chargerServer() throws RemoteException {
		OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		return ((com.sun.management.OperatingSystemMXBean) os).getProcessCpuLoad();

	}

	public String getServiceName() throws RemoteException {
		return _serviceName;
	}

	public Map<String, Integer> getAccounts() throws RemoteException {
		return accounts;
	}

	public int getidTransaction() throws RemoteException {
		return idTransaction;
	}

}
