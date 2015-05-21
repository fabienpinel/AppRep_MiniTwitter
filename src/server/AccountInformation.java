package server;
import java.rmi.*;
/**
 * Created by Fabien on 07/05/15.
 */
public interface AccountInformation extends Remote{
    /**
     * Méthode permettant à l'utilisateur de se connecter
     * @param pseudo pseudo de l'utilisateur nécessaire à la connexion
     * @param password mot de passe de l'utilisateur nécessaire à la connexion
     * @return boolean oui ou non connecté
     * @throws RemoteException
     */
    public boolean connect(String pseudo, String password) throws RemoteException;
    public boolean createAccount(String pseudo, String password) throws RemoteException;

}
