package client;

import org.apache.activemq.ActiveMQConnectionFactory;
import server.AccountInformation;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.HashMap;

/**
 * Created by Fabien on 07/05/15.
 */
public class User implements javax.jms.MessageListener {

    private String pseudo;
    private String password;
    private boolean isConnected;
    int port;

    private Map<String, Message> receivedMessages;
    private List<String> topicAlreadySubscribed;

    //JMS declarations
    private javax.jms.Session receiveSession = null;
    private javax.jms.Connection connect = null;

    private TweetIDGenerator idGenerator;
    private Map<String, MessageConsumer> followings = null;
	private AccountInformation req;

	/**
     * Un utilisateur est identifié par un pseudo et un mot de passe
     *
     * @param pseudo   pseudo de l'utilisateur
     * @param password mot de passe de l'utilisateur
     */
    public User(String pseudo, String password, String jmsHost){
        this.pseudo = pseudo;
        this.password = password;
        this.isConnected = false;
        this.idGenerator = new TweetIDGenerator();
        this.topicAlreadySubscribed = new ArrayList<>();
        this.followings = new HashMap<>();

        this.receivedMessages = new HashMap<>();

        // Create a connection.
		String jmsFullURI = "tcp://"+jmsHost+":61616";
        ConnectionFactory factory = new ActiveMQConnectionFactory("user", "user", jmsFullURI);
        try {
            connect = factory.createConnection("user", "user");
        } catch (JMSException e) {
			System.err.println("Could not connect to ActiveMQ at "+jmsFullURI+" with credentials user/user.");
			System.err.println("Is ActiveMQ server running?");
			System.exit(0);
        }
    }

    /**
     * Création d'un compte sur le serveur RMI
     * @param username
     * @param pass
     * @param port
     * @return
     */
    public static boolean createAccount(String username, String pass, int port){
        try {
			System.out.println("(Connecting to RMI @ "+ConsoleClient.RMI_IP);
			Registry r = LocateRegistry.getRegistry(ConsoleClient.RMI_IP, port);
            AccountInformation req = (AccountInformation) r.lookup("Server");
            return(req.createAccount(username, pass));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Méthode de connexion de l'utilisateur.
     * Interogation auprès du serveur rmi sur la classe AccountInformationImpl et la methode connect
     *
     * @param port port du serveur rmi (2002 par exemple)
     * @return true ou false indiquant si la connexion a réussie ou non
     */
    public boolean connect(int port) {
        this.port = port;
        if (isConnected) {
            return true;
        }
        try {
            Registry r = LocateRegistry.getRegistry(ConsoleClient.RMI_IP, port);
            req = (AccountInformation) r.lookup("Server");
            if(req.connect(pseudo, password)){
                //configurer jms server puis start ?
                this.configurerConsommateur();
                this.joinTopic(this.getUsername());
                this.joinTopic("#"+this.getPseudo()+"_favorites");
                this.afficherTopicsDisponibles();
                this.setIsConnected(true);
				this.loadPreviousTopics(req.getUserFollowedTopics(pseudo));
                return true;
            } else {
				System.err.println("Invalid credentials");
				return false;
			}
            //receiveSession.createTopic("#basic");
            //this.joinTopic("#basic");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.fillInStackTrace());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void afficherTopicsDisponibles(){
        Registry r = null;
        List<String> topics=null;
        try {
            r = LocateRegistry.getRegistry(ConsoleClient.RMI_IP, port);
            AccountInformation req = (AccountInformation) r.lookup("Server");
            topics = req.getTopicList();
			System.out.println("=== Topics disponibles sur ce serveur ===");
			for(String s : topics){
                System.out.println("\t"+s);
            }
			System.out.println("=========================================");
		} catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    /*  GETTERS AND SETTERS */
    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String Password) {
        this.password = password;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    private void configurerConsommateur() throws JMSException {
        // On identifie l'utilisateur auprès de JMS pour pouvoir récupérer les anciens messages
        connect.setClientID(this.getUsername());

        // Pour consommer, il faudra simplement ouvrir une session
        receiveSession = connect.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        MessageConsumer testConsumer = receiveSession.createConsumer(receiveSession.createTopic("#test"));
        testConsumer.setMessageListener(this);

        connect.start();
        //queue = receiveSession.createQueue("tweetsQueue");
        //javax.jms.MessageConsumer qReceiver = receiveSession.createConsumer(queue);
        //qReceiver.setMessageListener(this);

        // Now that 'receive' setup is complete, start the Connection
    }

    @Override
    public void onMessage(Message message) {
        //System.out.println("Reception message: "+message.toString());
        try {
            String id = message.getJMSCorrelationID();
            // If the message has already been received, don't display it.
            if (receivedMessages.keySet().contains(id)) {
                return;
            }
            // Store the message
            receivedMessages.put(id, message);

            // Display the message
            if (message instanceof MapMessage) {
                MapMessage msg = (MapMessage) message;
                System.out.println("Message de "+msg.getString("author")+" sur "+msg.getString("topic")+":"
						+msg.getString("content"));
            }
            else if (message instanceof TextMessage) {
                TextMessage msg = (TextMessage) message;
                System.out.println("Reading message: " +
                        msg.getText());
            } else {
				System.out.println("Message of wrong type");
            }
        }
        catch (JMSException e) {
            System.out.println("JMSException in onMessage(): " + e.toString());
        }
    }

    public void joinTopic(String hashtagName) {
        try {
            if(!this.topicAlreaySubscribed(hashtagName)){
                Topic t = receiveSession.createTopic(hashtagName);
                //ajout du nouveau topic coté RMI
				req.onTopicFollow(pseudo, hashtagName);
                addATopicToRMI(hashtagName);

                // On veut pouvoir récupérer les messages entre deux connexions -> durableSubscriber
                TopicSubscriber ds = receiveSession.createDurableSubscriber(t, "=" + hashtagName + "_" + this.getUsername());
                this.followings.put(hashtagName, ds);
                ds.setMessageListener(this);
                this.topicAlreadySubscribed.add(hashtagName);
            }
        } catch (JMSException e) {
            System.err.println("Could not create topic '" + hashtagName + "'");
            e.printStackTrace();
        } catch (RemoteException e) {
			System.err.println("Could not create topic '" + hashtagName + "'");
			e.printStackTrace();
		}
	}

    public void unfollowTopic(String topicName) {
        MessageConsumer mc = followings.get(topicName);
        try {
            mc.close();
            followings.remove(topicName);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void post(String tweet, String topic) throws JMSException, NamingException  {
        Topic t = receiveSession.createTopic(topic);
        MessageProducer mp = receiveSession.createProducer(t);
        MapMessage mess = receiveSession.createMapMessage();
        mess.setString("author", this.getPseudo());
		mess.setString("topic", topic);
        mess.setString("content", tweet);
        mess.setJMSCorrelationID(idGenerator.nextId());
        mp.send(mess);

        //On post également le message sur le topic de l'user courant
        receiveSession.createProducer(receiveSession.createTopic(this.getUsername())).send(mess);
    }

    public void addATopicToRMI(String topic){
        try {
           	req.registerANewTopic(topic);

            //On post également le topic sur le topic principal.
            MapMessage mess= receiveSession.createMapMessage();
            mess.setString("author", "administrator");
            mess.setString("content", topic);
            receiveSession.createProducer(receiveSession.createTopic("basic")).send(mess);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            System.out.println("Could not create topic :" + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui véifie si on est deja abonné à un hashtag ou non
     * @param topicname nom du topic
     * @return vrai ou faux deja abonné ou non 
     */
    public boolean topicAlreaySubscribed(String topicname){
        return this.topicAlreadySubscribed.contains(topicname);
    }
    public String getUsername(){
        return "@"+this.getPseudo();
    }


	public List<String> getFollowedHashTags() {
		List<String> result = new LinkedList<>();
		for (String s : topicAlreadySubscribed) {
			if (s.charAt(0) == '#') result.add(s);
		}
		return result;
	}

	public void loadPreviousTopics(List<String> userTopics) {
		System.out.println("Vous etes abonné à " + userTopics.size() + " topics:");
		for (String s : userTopics) {
			try {
				System.out.println("\t" + s);
				if (topicAlreadySubscribed.contains(s)) {
					//System.out.println("\t\tSkipped");
					continue;
				}

				Topic t = receiveSession.createTopic(s);
				// On veut pouvoir récupérer les messages entre deux connexions -> durableSubscriber
				TopicSubscriber ds = receiveSession.createDurableSubscriber(t, "=" + s + "_" + this.getUsername());
				this.followings.put(s, ds);
				ds.setMessageListener(this);
				this.topicAlreadySubscribed.add(s);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done loading previous topics.");
	}

	public void disconnect() {
		try {
			connect.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		/*for (String s : followings.keySet()) {
			MessageConsumer mc = followings.get(s);
			try {
				mc.setMessageListener(null);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}*/
	}
}
