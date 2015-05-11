package server;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBlobMessage;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by Fabien on 09/05/15.
 */
public class JMS_server implements javax.jms.MessageListener{

    private javax.jms.Connection connect = null;
    private javax.jms.Session sendSession = null;

    private javax.jms.MessageProducer sender = null;
    private javax.jms.Queue queue = null;

    public void configurer() {

        try
        {	// Create a connection.
            javax.jms.ConnectionFactory factory;
            factory = new ActiveMQConnectionFactory("user", "user", "tcp://localhost:61616");
            connect = factory.createConnection ("user", "user");
            // ce programme est donc en mesure d'accéder au broker ActiveMQ, avec connecteur tcp (openwire)
            // Si le producteur et le consommateur étaient codés séparément, ils auraient eu ce même bout de code

            this.configurerProducteur();
            //this.configurerConsommateur();

        } catch (javax.jms.JMSException jmse){
            jmse.printStackTrace();
        }
        //this.produire();
    }
    public void start(){
        try {
            connect.start(); // on peut activer la connection.
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    private void configurerProducteur() throws JMSException {
        // Dans ce programme, on decide que le producteur crée la queue

        //La queue etant crée, il peut y accéder en mode producteur, au sein d'une session
        sendSession = connect.createSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
        queue = sendSession.createQueue ("queue");
        sender = sendSession.createProducer(queue);
    }


    private void produire(String message, int topicNumber){
        //Fabriquer un message
        //System.out.println("Production de message.");
        //Poster ce message dans la queue
        try {
            sender.send(new ActiveMQBlobMessage());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        // Methode permettant au consommateur de consommer effectivement chaque msg recu via la queue
        System.out.println("Recu un message de la queue "+message.toString());
    }

}
