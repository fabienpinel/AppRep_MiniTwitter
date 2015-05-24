package server;

import client.Console;
import client.ConsoleClient;

import java.util.Scanner;

/**
 * Created by Fabien on 07/05/15.
 */
public class Main {
    public static void main(String args[]){
        System.out.println("AppRep_MiniTwitter - Main Server");
        System.out.println("Lancement du serveur");
        Server server = null;

		Console c = new Console();
		//server = new Server("localhost", 2002);
		Scanner sc = new Scanner(System.in);
		String ipOnNetwork = sc.nextLine();

		System.setProperty("java.rmi.server.hostname", "192.168.0.45");
		server = new Server("127.0.0.1", 2002);

		System.out.println("Démarrage du JMS");
        server.run();

		System.out.println("Chargement des topics utilisateurs...");
		server.loadConfig();
		System.out.println("Topics chargés!");

    }
}
