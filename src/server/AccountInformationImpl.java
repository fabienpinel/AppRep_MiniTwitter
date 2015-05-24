package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by Fabien on 07/05/15.
 * La connexion de l'utilisateur au système se fera en Java RMI, auprès d'un objet RMI serveur (qui maintient les comptes)
 */
public class AccountInformationImpl extends UnicastRemoteObject implements AccountInformation {

    //hashmap contenant les comptes (pseudo/mdp)
    private HashMap<String , String> accounts;
    private List<String> topics;
	private Map<String, List<String>> followedTopics;

    /**
     * Constructeur de AccountInformationImpl
     * Instanciation des variables et appel à l'initialisation de la hashmap
     * @throws RemoteException
     */
    public AccountInformationImpl() throws RemoteException {
        super();
        this.accounts = new HashMap<String , String>();
        this.topics = new ArrayList<String>();
        this.initHashMapAccounts();

		this.followedTopics = new HashMap<>();
    }

    /**
     * Initialisation de la hashmap des utilisateurs avec des informations de test
     */
    private void initHashMapAccounts(){
        this.accounts.put("fabien", "pass");
        this.accounts.put("test", "test");
    }

    /**
     *  Méthode procédant à la vérification des informations utilisateurs
     *  Il s'agit d'une méthode qui sera appelée à distance via RMI
     * @param pseudo pseudo de l'utilisateur nécessaire à la connexion
     * @param password mot de passe de l'utilisateur nécessaire à la connexion
     * @return true ou false existant dans la liste des utilisateurs ou non
     * @throws RemoteException
     */
    @Override
    public boolean connect(String pseudo, String password) throws RemoteException {
        if(accounts.containsKey(pseudo)){
            return accounts.get(pseudo).equals(password);
        }
        return false;
    }

    @Override
    public boolean createAccount(String pseudo, String password) throws RemoteException {
        this.accounts.put(pseudo, password);
        return true;
    }

    @Override
    public void registerANewTopic(String topicName) throws RemoteException {
        if(!this.topics.contains(topicName)){
            this.topics.add(topicName);
        }
    }

    @Override
    public List<String> getTopicList() throws RemoteException {
        return this.topics;
    }

	private void persistFollowedTopics() {
		// TODO: persist
	}

	@Override
	public void onTopicFollow(String pseudo, String topicName) throws RemoteException {
		System.out.println("Adding topic "+topicName+" to user "+pseudo);
		// Get the list of followed topics by the user
		List<String> userFollowedTopics = followedTopics.get(pseudo);
		if (userFollowedTopics == null) {
			userFollowedTopics = new LinkedList<>();
			followedTopics.put(pseudo, userFollowedTopics);
		}

		if (! userFollowedTopics.contains(topicName)) {
			userFollowedTopics.add(topicName);
			persistFollowedTopics();
		}
	}

	@Override
	public List<String> getUserFollowedTopics(String pseudo) throws RemoteException {
		List<String> result = followedTopics.get(pseudo);
		if (result == null) result = new LinkedList<>();
		return result;
	}

	private String serialize() {
		String s = "";
		for (String username : followedTopics.keySet()) {
			String row = username+":";
			try {
				for (String topicName : getUserFollowedTopics(username)) {
					row += topicName+":";
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			row += "\n";
			s += row;
		}
		return s;
	}

}
