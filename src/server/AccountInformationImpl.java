package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * Created by Fabien on 07/05/15.
 * La connexion de l'utilisateur au système se fera en Java RMI, auprès d'un objet RMI serveur (qui maintient les comptes)
 */
public class AccountInformationImpl extends UnicastRemoteObject implements AccountInformation {

    //hashmap contenant les comptes (pseudo/mdp)
    private HashMap<String , String> accounts;

    /**
     * Constructeur de AccountInformationImpl
     * Instanciation des variables et appel à l'initialisation de la hashmap
     * @throws RemoteException
     */
    public AccountInformationImpl() throws RemoteException {
        super();
        this.accounts = new HashMap<String , String>();
        this.initHashMapAccounts();
    }

    /**
     * Initialisation de la hashmap des utilisateurs avec des informations de test
     */
    private void initHashMapAccounts(){
        this.accounts.put("fabien", "pass");
        this.accounts.put("test", "test");
    }

    /**
     *  Méthode procédant à la vérification des informations utilisateurs
     *  Il s'agit d'une méthode qui sera appelée à distance via RMI
     * @param pseudo pseudo de l'utilisateur nécessaire à la connexion
     * @param password mot de passe de l'utilisateur nécessaire à la connexion
     * @return true ou false existant dans la liste des utilisateurs ou non
     * @throws RemoteException
     */
    @Override
    public boolean connect(String pseudo, String password) throws RemoteException {
        if(accounts.containsKey(pseudo)){
            return accounts.get(pseudo).equals(password);
        }
        return false;
    }

    @Override
    public boolean createAccount(String pseudo, String password) throws RemoteException {
        this.accounts.put(pseudo, password);
        return true;
    }
}
