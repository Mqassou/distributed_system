package framework.service;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * The Sorter interface defines a service that <code>sort</code> and <code>reverseSort</code> lists of <code>String</code>s.
 *
 * As a <b>remote</b> interface, Sorter must:
 * <ul>
 * <li>extends the <code>Remote</code> interface,
 * <li>have all its methods throw <code>RemoteException</code>.
 * </ul>
 *
 */
public interface Bank extends Service {

	public void createAccount(String accountName) throws RemoteException, NotBoundException;

	public void createAccountReplica(String accountName) throws RemoteException;

	public int checkBalance(String accountName) throws RemoteException, NotBoundException;

	public int checkBalanceReplica(String accountName) throws RemoteException;

	public void depositWithdraw(int money, String accountName) throws RemoteException, NotBoundException;

	public void depositWithdrawReplica(int money, String accountName) throws RemoteException;

	public void notifyReplicas(BankTransaction bt) throws RemoteException;

	public void updateTransactionsQueue() throws RemoteException;

	public Runnable periodicUpdate() throws RemoteException;

	public void updateSecondaryReplicas(Map<String, Integer> accounts) throws RemoteException;

	public Map<String, Integer> getAccounts() throws RemoteException;

	public int getidTransaction() throws RemoteException;
}