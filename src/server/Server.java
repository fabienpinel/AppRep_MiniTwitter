package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	public void loadConfig() {
		String dir = System.getProperty("user.home");
		System.out.println("User home:"+dir);
	}

	private Map<String, List<String>> deserialize(String input) {
		Map<String, List<String>> map = new HashMap<>();
		String[] lines = input.split("\n");
		System.out.println("Deserializing from "+lines.length+" lines");
		for (String l : lines) {
			String[] parts = l.split(":");
			String username = parts[0];
			List<String> userFollowedTopics = new LinkedList<>();
			for (int i = 1; i < parts.length; ++i) {
				userFollowedTopics.add(parts[i]);
			}
			map.put(username, userFollowedTopics);
		}

		return map;
	}
}
