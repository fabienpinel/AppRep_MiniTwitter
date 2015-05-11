package client;

/**
 * Created by Fabien on 07/05/15.
 */
public class Main {
    public static void main(String args[]){
        System.out.println("AppRep_MiniTwitter - Main Client");
        System.out.println("Lancement du client");
        ConsoleClient cc = new ConsoleClient();
        cc.run(2002);
    }
}
