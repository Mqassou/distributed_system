package framework.service;

import java.io.Serializable;

public class BankTransaction implements Serializable {

	private static final long serialVersionUID = 1L;
	private String _accountName;
	private int _amountTransaction;
	private int _idTransaction;
	private TransactionType _typeTransaction;

	@Override
	public String toString() {
		return "BankTransaction [_accountName=" + _accountName + ", _amountTransaction=" + _amountTransaction + ", _idTransaction=" + _idTransaction + ", _typeTransaction=" + _typeTransaction + "]";
	}

	public enum TransactionType {
		depositWithdraw, createAccount
	}

	public BankTransaction(String accountName, int amountTransaction, int idTransaction, TransactionType typeTransaction) {
		_accountName = accountName;
		_amountTransaction = amountTransaction;
		_idTransaction = idTransaction;
		_typeTransaction = typeTransaction;

	}

	public BankTransaction(String accountName, int idTransaction, TransactionType typeTransaction) {
		_accountName = accountName;
		_idTransaction = idTransaction;
		_typeTransaction = typeTransaction;
	}

	public String get_accountName() {
		return _accountName;
	}

	public void set_accountName(String _accountName) {
		this._accountName = _accountName;
	}

	public int get_amountTransaction() {
		return _amountTransaction;
	}

	public void set_amountTransaction(int _amountTransaction) {
		this._amountTransaction = _amountTransaction;
	}

	public int get_idTransaction() {
		return _idTransaction;
	}

	public void set_idTransaction(int _idTransaction) {
		this._idTransaction = _idTransaction;
	}

	public TransactionType get_typeTransaction() {
		return _typeTransaction;
	}

	public void set_typeTransaction(TransactionType typeTransaction) {
		_typeTransaction = typeTransaction;
	}

}
