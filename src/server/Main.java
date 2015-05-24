package server;

import client.Console;
import client.ConsoleClient;

/**
 * Created by Fabien on 07/05/15.
 */
public class Main {
    public static void main(String args[]){
        System.out.println("AppRep_MiniTwitter - Main Server");
        System.out.println("Lancement du serveur");
        Server server = null;

		Console c = new Console();
		server = new Server("localhost", 2002);

		System.out.println("Démarrage du JMS");
        server.run();

		System.out.println("Chargement des topics utilisateurs...");
		server.loadConfig();

    }
}
