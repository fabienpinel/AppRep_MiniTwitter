package server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	private AccountInformationImpl service;

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
            service = new AccountInformationImpl();
            LocateRegistry.createRegistry(port);
            Naming.rebind("rmi://" + host + ":" + port + "/Server", service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static String getAppDir() {
		return System.getProperty("user.home")+"/.miniTwitter_gpr";
	}

	public static String getUsersTopicsFile() {
		return getAppDir()+"/users_topics.data";
	}

	public void loadConfig() {
		String dir = getAppDir();
		String path = getUsersTopicsFile();

		try {
			// Create dir if needed
			Path dirP = Paths.get(dir);
			if (!Files.exists(dirP)) {
				Files.createDirectory(dirP);
			}
			// Create file if needed
			File yourFile = new File(path);
			if(!yourFile.exists()) {
				yourFile.createNewFile();
			}
		} catch (IOException e) {
			System.out.println("Could not read configuration file.");
			e.printStackTrace();
			System.exit(0);
		}

		// Read users data from file
		try {
			List<String> content = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
			service.setFollowedTopics(deserializeUserTopics(content));
		} catch (IOException e) {
			System.out.println("Could not read from config file!");
			e.printStackTrace();
		}
	}

	private Map<String, List<String>> deserializeUserTopics(List<String> lines) {
		Map<String, List<String>> map = new HashMap<>();
		for (String l : lines) {
			System.out.println("\tParsing: "+l);
			String[] parts = l.split(":");
			String username = parts[0];
			List<String> userFollowedTopics = new LinkedList<>();
			for (int i = 1; i < parts.length; ++i) {
				System.out.println("\t\tAdding topic"+parts[i]+" to user "+username);
				userFollowedTopics.add(parts[i]);
			}
			map.put(username, userFollowedTopics);
		}

		return map;
	}
}
