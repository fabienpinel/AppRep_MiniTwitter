package client;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.util.List;

/**
 * Created by Fabien on 09/05/15.
 */
public class ConsoleClient {
    //Longueur maximale d'un tweet
    public static int LONGUEUR_MAXIMALE_TWEET = 140;

    //console permettant de récupérer ce que l'utilisateur tape
    private Console console = null;
    //tableaux contenant les choix disponibles
    private String[] choicesConnect;
    private String[] choicesAlreadyConnected;
    //utilisateur
    protected User user = null;
    private int port;

    public ConsoleClient(){
        this.console = new Console();
        this.choicesConnect = new String[2];
        this.choicesAlreadyConnected = new String[4];
        this.initChoices();
    }

    /**
     * Initialisation des choix disponibles (choix non connecté, choix connecté)
     */
    private void initChoices(){
        this.choicesConnect[0] = "Se connecter";
        this.choicesConnect[1] = "Quitter";
        this.choicesAlreadyConnected = new String[] {
				"Poster Un message",
				"Créer un nouveau hashtag",
				"S'abonner à un hashtag",
				"Se désabonner à un hashtag",
				"Se déconnecter",
				"Quitter"
		};
    }

    /**
     * Affichage d'un tableau de string
     * @param choices tableau de string contenant les choix disponibles
     */
    public void displayChoices(String[] choices){
        for(int i=0; i<choices.length; i++){
            System.out.println(""+i+". "+choices[i]);
        }
    }

    /**
     * Tentative de connexion
     * instanciation de l'utilisateur et appel de la methode connect de User
     * @param port port du serveur rmi
     */
    public void connect(int port){
        //pseudo + mdp
        System.out.println("Entrez votre pseudo: ");
        String pseudo = this.console.getNextLine();
        System.out.println("Entrez votre password: ");
        String password = this.console.getNextLine();
        //appel à methode connect de user
		System.out.print("JMS Host IP:");
		String jmsHost = this.console.getNextLine("localhost");
        user = new User(pseudo, password, jmsHost);
        user.connect(port);

        if(this.user.isConnected()){
            //si connect , appel à method runConnected
            this.runConnected();
        }else{
            System.out.println("Echec de l'authentification");
        }
    }

    /**
     * Vérification de la validité du hashtag
     * # au début
     * @param hashtag string du hashtag
     * @return vrai ou faux suivant la validité
     */
    public boolean checkValidHashtag(String hashtag){
        return ((hashtag.charAt(0)=='#'));
    }

    /**
     * Création d'un nouvel hashtag
     * Méthode seulement accessible lorsque l'utilisateur est connecté
     */
    public void createNewHashtag(){
        boolean hashtagValid = false;
		String hashtag;
		do {
			System.out.println("Quel hashtag voulez vous créer ? (écrire le nom avec le # au début)");
			hashtag = this.console.getNextLine();
			hashtagValid = this.checkValidHashtag(hashtag);
			if (!hashtagValid) {
				System.out.printf("Le hashtag est invalide. (doit commencer par #)");
			}
		}
        while(!hashtagValid);

        //enregistrement du nouveau hashtag
        System.out.println("Enregistrement du nouveau hashtag...");
        //inscription de l'hashtag auprès du JMS
		user.joinTopic(hashtag);
		System.out.println("Hashtag enregistré!");
	}

    /**
     * Verification de la validité d'un tweet
     * notamment vérification de la longueur maximale
     * @param tweet
     * @return
     */
    private boolean checkValidityTweet(String tweet){
        return (tweet.length() <= LONGUEUR_MAXIMALE_TWEET );
    }



    /**
     * Poster un message sur un topic JMS
     */
    public void postMessage(){
        boolean messageCorrect = false;
        String topic = "";
        String tweet = "";
        while(!messageCorrect){
            topic = this.readHashTag("Saisissez votre topic(hashtag):");
            user.joinTopic(topic);
            System.out.println("Ecrivez votre message:");
            tweet = this.console.getNextLine();
            if(this.checkValidityTweet(tweet)) {
                messageCorrect = true;
            }
        }
        try {
            this.user.post(tweet,topic);
			System.out.println("Message posté!");
        } catch (JMSException e) {
            System.out.println("Cannot post this message");
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        //Poster le message

    }

    /**
     * Méthode permettant d'afficher le menu de choix lorsque l'utilisateur n'est pas connecté
     * @param port
     */
    public void run(int port){
        this.port = port;
        boolean choiceValid = false;
        this.console.sayHello();

        while (!choiceValid) {
            this.displayChoices(this.choicesConnect);
            int choice = Integer.parseInt(this.console.getNextLine());
            switch (choice) {
                case 0:
                    //se connecter
                    this.connect(port);
                    choiceValid = true;
                    break;
                case 1:
                    //quitter
                    choiceValid = true;
                    this.console.sayGoodbye();
                    break;
                default:
                    System.out.println("choix non reconnu");
                    choiceValid = false;
            }
        }
    }

    /**
     * Méthode permettant d'afficher le menu de choix d'option lorsque l'utilisateur est connecté
     */
    public void runConnected(){
        boolean choiceValid = false;
        System.out.println("Vous êtes connecté.");
        while (!choiceValid || this.user.isConnected()) {
            this.displayChoices(this.choicesAlreadyConnected);
            int choice = Integer.parseInt(this.console.getNextLine());
			choiceValid = true;
            switch (choice) {
                case 0:
                    //poster un msg
                    this.postMessage();
                    break;
                case 1:
                    //creer un nvx hashtag
                    this.createNewHashtag();
                    break;
				case 2:
					// s'abonner
					this.followHashtag();
					break;
				case 3:
					// se désabonner
					this.unfollowHashtag();
					break;
                case 5:
                    //se deconnecter
                    System.out.println("Déconnexion.");
                    this.user.setIsConnected(false);
                    this.run(this.port);
                    break;
                case 6:
                    //quitter
                    this.user.setIsConnected(false);
                    this.console.sayGoodbye();
                    break;
                default:
                    System.out.println("choix non reconnu");
                    choiceValid = false;
            }
        }
    }

	public String readHashTag(String message) {
		String hashtag = null;
		boolean isHashtagValid = false;
		while (! isHashtagValid) {
			System.out.print(message);
			hashtag = this.console.getNextLine();
			isHashtagValid = this.checkValidHashtag(hashtag);
			if (!isHashtagValid) {
				System.out.println("Hashtag invalide. Doit commencer par #");
			}
		}
		return hashtag;
	}

	public int readInt(String message) {
		while (true) {
			System.out.println(message);
			try {
				return Integer.parseInt(this.console.getNextLine());
			} catch (NumberFormatException e) {
				System.out.println("Vous devez entrer un nombre.");
			}
		}
	}

	public void followHashtag() {
		System.out.println("S'abonner à un hashtag:");
		String hashtag = readHashTag("Entrez le nom du hashtag à suivre:");
		user.joinTopic(hashtag);
	}

	public void unfollowHashtag() {
		System.out.println("Se désabonner d'un hashtag:");
		List<String> hashtags = user.getFollowedHashTags();
		System.out.println("Followed hashtags:"+hashtags.size());
		for (int i = 0; i< hashtags.size(); ++i) {
			System.out.println("\t"+(i+1)+" - "+hashtags.get(i));
		}
		int i = readInt("Entrez le numero du hashtag duquel se désabonner:");
		String hashtag = hashtags.get((i-1));
		user.unfollowTopic(hashtag);
	}
}
