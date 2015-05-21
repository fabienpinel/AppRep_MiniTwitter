####################    README  ####################
Fabien Pinel
pinel@polytech.unice.fr
SI4 - Groupe info3

IDE utilisé :   IntelliJ Idea 14.1.1
Version de Java utilisée :  Java 1.7.0
####################    FONCTIONNALITES
+   Console client interactive
+   Connexion grace à un appel RMI afin de vérifier le compte
-   Poster un message sur un hastag
-   S'abonner à un hashtag
-	S'abonner à un utilisateur

####################    GRANDES LIGNES SUIVIES POUR ORGANISER LE CODE
J'ai commencé par développé la console client interactive.
J'ai donc écrit des fonctions vide devant faire appel soit à RMI soit à JMS et j'y ai écrit des TODOs
Ensuite j'ai donc déroulé les TODOs de ma console client et ajouté les fonctionnalités  au fur et à mesure.
De cette façon j'ai développé console client puis connexion RMI puis envoi et réception de donnée JMS

####################    DIFFICULTES RENCONTREES
Le développpement de la console s'est fait assez facilement car rien de nouveau ni de complexe.
Le développement de la partie loging/comptes en RMI s'est aussi bien passé car nous avons fait plusieurs fois du RMI auparavant.
Le développement de la partie JMS a été plus complexe et m'a demandé plus de travail et débugage car c'était assez nouveau ...

TO BE CONTINUED

Pour lancer correctement le projet, il faut :
    -> Lancer activemq :
        --> "cd acivemq/bin"
        --> "./activemq console"
    -> Lancer le Main du serveur (Classe Main dans package server)
    -> Lancer le Main du client (Classe Main dans package client)


