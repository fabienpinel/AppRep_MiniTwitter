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
	private Action[] actionsConnect;
	private Action[] actionsAlreadyConnected;
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
		this.actionsConnect = new Action[] {
				new Action("Créer un compte") {
					@Override
					public void execute() {
						createAccount(port);
						run(port);
					}
				},
				new Action("Se connecter") {
					@Override
					public void execute() {
						connect(port);
					}
				},
				new Action("Quitter") {
					@Override
					public void execute() {
						quit();
					}
				}
		};
        /*this.choicesConnect[0] = "Se connecter";
        this.choicesConnect[1] = "Quitter";
        this.choicesAlreadyConnected = new String[] {
				"Poster Un message",
				"Créer un nouveau hashtag",
				"S'abonner à un hashtag",
				"Se désabonner à un hashtag",
				"Se déconnecter",
				"Quitter"
		};*/
		this.actionsAlreadyConnected = new Action[] {
				new Action("Poster un message") {
					@Override
					public void execute() {
						postMessage();
					}
				},
				new Action("Créer un nouveau hashtag") {
					@Override
					public void execute() {
						createNewHashtag();
					}
				},
				new Action("S'abonner à un hashtag") {
					@Override
					public void execute() {
						followHashtag();
					}
				},
                new Action("S'abonner à une personne") {
                    @Override
                    public void execute() {
						followPerson();
                    }
                },
				new Action("Se désabonner d'un hashtag") {
					@Override
					public void execute() {
						unfollowHashtag();
					}
				},
				new Action("Se déconnecter") {
					@Override
					public void execute() {
						disconnect();
					}
				},
				new Action("Quitter") {
					@Override
					public void execute() {
						quit();
					}
				}
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
     * Tentative de connexion
     * instanciation de l'utilisateur et appel de la methode connect de User
     * @param port port du serveur rmi
     */
    public boolean createAccount(int port){
        //pseudo + mdp
        System.out.println("Entrez votre pseudo: ");
        String pseudo = this.console.getNextLine();
        System.out.println("Entrez votre password: ");
        String password = this.console.getNextLine();
        return User.createAccount(pseudo, password, port);
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
     * Vérification de la validité d'une personne
     * @ au début
     * @param person  @ suivi du nom de la personne
     * @return vrai ou faux suivant la validité
     */
    public boolean checkValidPerson(String person){
        return ((person.charAt(0)=='@'));
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
		this.console.sayHello();
		displayMenu("", this.actionsConnect);
    }

    /**
     * Méthode permettant d'afficher le menu de choix d'option lorsque l'utilisateur est connecté
     */
    public void runConnected(){
        boolean choiceValid = false;
        System.out.println("Vous êtes connecté.");
		while (this.user.isConnected()) {
			displayMenu("", actionsAlreadyConnected);
		}
    }

	public void displayMenu(String message, Action[] actions) {
		if (message.length() > 0) System.out.println(message);
		for(int i = 0; i< actions.length; ++i) {
			System.out.println("\t"+(i+1)+" - "+actions[i].getName());
		}
		int choice = readInt("Choisissez l'action à effectuer:", 1, actions.length);
		actions[choice - 1].execute();
	}

	public void disconnect() {
		System.out.println("Déconnexion.");
		this.user.setIsConnected(false);
		this.run(this.port);
	}

	public void quit() {
		this.user.setIsConnected(false);
		this.console.sayGoodbye();
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

    public String readPerson(String message) {
        String person = null;
        boolean isPersonValid = false;
        while (! isPersonValid) {
            System.out.print(message);
            person = this.console.getNextLine();
            isPersonValid = this.checkValidPerson(person);
            if (!isPersonValid) {
                System.out.println("person invalide. Doit commencer par @");
            }
        }
        return person;
    }

	public int readInt(String message) {
		return readInt(message, 0, 0);
	}

	public int readInt(String message, int start, int end) {
		while (true) {
			System.out.println(message);
			try {
				int value = Integer.parseInt(this.console.getNextLine());
				if (start != end && (value < start || value > end)) {
					System.out.println("Vous devez entrer une valeur entre "+start+" et "+end+".");
					continue;
				}
				return value;
			} catch (NumberFormatException e) {
				System.out.println("Vous devez entrer un nombre.");
			}
		}
	}

	public void followHashtag() {
		System.out.println("S'abonner à un hashtag:");
		String hashtag = readHashTag("Entrez le nom du hashtag à suivre:");
		user.joinTopic(hashtag);
		System.out.println("Vous suivez maintenant le hashtag "+hashtag);
	}

    public void followPerson() {
        System.out.println("S'abonner à une personne:");
        String person = readPerson("Entrez le nom d'une personne à suivre:");
        user.joinTopic(person);
        System.out.println("Vous suivez maintenant la personne " + person);
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
