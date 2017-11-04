package com.perso.config;

import org.hibernate.TransactionException;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

import javax.transaction.*;

public class AtomikosJtaPlatform implements JtaPlatform {
    private static final long serialVersionUID = 1L;
    public static TransactionManager txMgr;
    public static UserTransaction userTx;

    public AtomikosJtaPlatform() {
    }

    public Object getTransactionIdentifier(final Transaction transaction) {
        return transaction;
    }

    public void registerSynchronization(final Synchronization synchronization) {
        try {
            txMgr.getTransaction().registerSynchronization(synchronization);
        } catch (Exception var3) {
            throw new JtaPlatformException("Could not access JTA Transaction to register synchronization", var3);
        }
    }

    public boolean canRegisterSynchronization() {
        try {
            if (txMgr.getTransaction() != null) {
                return txMgr.getTransaction().getStatus() == 0;
            } else {
                return false;
            }
        } catch (SystemException var2) {
            throw new TransactionException("Could not determine transaction status", var2);
        }
    }

    public int getCurrentStatus() throws SystemException {
        return this.retrieveTransactionManager().getStatus();
    }

    public UserTransaction retrieveUserTransaction() {
        return userTx;
    }

    public TransactionManager retrieveTransactionManager() {
        return txMgr;
    }
}