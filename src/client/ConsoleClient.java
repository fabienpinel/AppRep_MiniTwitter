package client;

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
        this.choicesAlreadyConnected[0] = "Poster Un message";
        this.choicesAlreadyConnected[1] = "Créer un nouveau hashtag";
        this.choicesAlreadyConnected[2] = "Se déconnecter";
        this.choicesAlreadyConnected[3] = "Quitter";
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
		user.createHashtag(hashtag);
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
     * Verification de la validité du topic.
     * le numero du topic doit correspondre a un topic existant
     * @param topic numero de topic
     * @return true ou false valid ou non
     */
    private boolean checkValidityTopic(String topic){
        //TODO verifier que le nom de topic est correct (qu'il existe)
        return this.user.verifyIfTopicExists(topic);
    }


    /**
     * Poster un message sur un topic JMS
     */
    public void postMessage(){
        boolean messageCorrect = false;
        boolean topicCorrect = false;
        String topic = "";
        String tweet = "";
        while(!messageCorrect || !topicCorrect){
            System.out.println("Saisissez votre topic(hashtag):");
            topic = this.console.getNextLine();
            if(this.checkValidityTopic(topic)){
                topicCorrect=true;
            }else{
                user.createHashtag(topic);
            }
            System.out.println("Ecrivez votre message:");
            tweet = this.console.getNextLine();
            if(this.checkValidityTweet(tweet)){
                messageCorrect = true;
            }

        }
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
        while (!choiceValid && this.user.isConnected()) {
            this.displayChoices(this.choicesAlreadyConnected);
            int choice = Integer.parseInt(this.console.getNextLine());
            switch (choice) {
                case 0:
                    //poster un msg
                    this.postMessage();
                    choiceValid = true;
                    break;
                case 1:
                    //creer un nvx hashtag
                    this.createNewHashtag();
                    choiceValid = true;
                    break;
                case 2:
                    //se deconnecter
                    System.out.println("Déconnexion.");
                    this.user.setIsConnected(false);
                    this.run(this.port);
                    break;
                case 3:
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
}
