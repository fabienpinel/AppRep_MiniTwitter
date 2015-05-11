package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Fabien on 07/05/15.
 * La connexion de l'utilisateur au système se fera en Java RMI, auprès d'un objet RMI serveur (qui maintient les comptes)
 */
public class Server{
    String host;
    int port;

    /**
     * Serveur RMI
     * @param host adresse hote du serveur rmi (localhost...)
     * @param port port du serveur rmi (2002 par exemple)
     */
    public Server(String host, int port)  {
        this.host = host;
        this.port = port;
    }

    /**
     * Démarrage du serveur et instanciation du système de compte
     */
    public void run(){
        try {
            AccountInformationImpl as = new AccountInformationImpl();
            LocateRegistry.createRegistry(port);
            Naming.rebind("rmi://" + host + ":" + port + "/Server", as);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
