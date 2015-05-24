####################    README  ####################
GUILLERMIN Tom
PINEL Fabien
ROUMEGUE Jeremy

SI4 - Groupe info3

IDE utilisé :   IntelliJ Idea 14.1.1
Version de Java utilisée :  Java 1.7.0

####################    FONCTIONNALITES
+   Console client interactive
+   Connexion grace à un appel RMI afin de vérifier le compte
+   Stockage des Topics (hashtags) existants côté RMI sous forme de liste
+   S'abonner à un hashtag (avec persistence dans des fichiers)
+   Se désabonner d'un hashtag
+   Poster un message sur un hastag
+   Recevoir les messages postés sur les topics auxquels nous sommes abonnés
+   Pouvoir recevoir les messages publiés en mon absence lors de ma connexion
+   Persistence des topics (hashtags) existants côté JMS

####################    FONCTIONNALITES AJOUTEES
+   Création de compte
    L'utilisateur peut créer un compte qui sera ajouté côté RMI.
    Le compte sera persisté entre 2 allumages du serveur, grâce à un fichier
+	S'abonner à un utilisateur
    Cela revient à s'abonner à un topic qui se nomme : @nomUtilisateur


####################    GRANDES LIGNES SUIVIES POUR ORGANISER LE CODE
Nous avons commencé par développer la console client interactive.
Nous avons donc écrit des fonctions vide devant faire appel soit à RMI soit à JMS correspondants aux spécifications de l'application
 et nous y avons écrit des TODOs.
Ensuite nous avons déroulé les TODOs de la console client et ajouté les fonctionnalités  au fur et à mesure.
De cette façon nous avons développé console client puis connexion RMI puis gestion des données JMS.

##########  DESCRIPTION DE LA STRUCTURE
PACKAGE Client
    Action  -   Interface
    Console -   Classe permettant de récupérer ce que tape l'utilisateur
    ConsoleClient - Classe principale gérant l'interaction avec le client (actions disponibles etc.)
    Main    -   Classe permettant de lancer le client
    TweetIDGenerator    -   Classe permettant de générer un ID unique dont on se sert pour identifier un tweet en interne
    User    -   Classe représentant un User en particulier. C'est cette classe qui intéragie avec RMI et JMS pour les actions.

PACKAGE Server
    AccountInformation - Interface accessible via RMI et donc qui étend Remote.
                         4 fonctions sont accessibles en RMI :
                            1/  connect
                            2/  createAccount
                            3/  registerANewTopic
                            4/  getTopicList

    AccountInformationImpl  -   Implémentation de AccountInformation (méthodes RMI)
    Main    -   Classe permettant de lancer le serveur.
    Server  -   Classe qui lance le serveur RMI et met à disposition les méthode de AccountInformation via le bind "Server"


####################    DIFFICULTES RENCONTREES
Le développpement de la console s'est fait assez facilement car rien de nouveau ni de complexe.
Nous avons tout de même amélioré notre version de la console en cours de route pour la simplifier.
Nous avons en effet utilisé la classe abstraite "Action" qui nous permet de simplifier le code de la console en le
rendant un peu plus générique pour l'affichage des menus.
Le développement de la partie login/comptes en RMI s'est aussi bien passé car nous avons fait plusieurs fois du RMI auparavant.
Le développement de la partie JMS a été plus complexe et nous a demandée plus de travail et débugage.
La mise en oeuvre n'était pas la partie la plus complexe ni chronophage, en revanche l'étude de la documentation de JMS, des exemples et le débugage nous a pris du temps.
Nous avons notamment passé du temps à rechercher comment obtenir une liste des topics existants auprès de JMS jusqu'à nous rendre compte que ce n'était pas vraiment possible.


Pour lancer correctement le projet, il faut :
    -> Lancer activemq :
        --> "cd acivemq/bin"
        --> "./activemq console"
    -> Lancer le Main du serveur (Classe Main dans package server)
    -> Lancer le Main du client (Classe Main dans package client)


