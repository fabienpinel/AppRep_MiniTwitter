package server;

/**
 * Created by Fabien on 07/05/15.
 */
public class Main {
    public static void main(String args[]){
        System.out.println("AppRep_MiniTwitter - Main Server");
        System.out.println("Lancement du serveur");
        Server server = null;
        server = new Server("localhost", 2002);
        server.run();

        System.out.println("Démarrage du JMS");

    }
}
