package client;

import java.util.Scanner;

/**
 * Created by Fabien on 09/05/15.
 */
public class Console {
    protected Scanner reader;
    protected String reponseUser;

    protected Console () {
        this.reader = new Scanner(System.in);
        this.reponseUser = "";
    }


    public  void sayHello(){
        System.out.println("Hello !");
    }
    public void sayGoodbye(){ System.out.println("Goodbye !"); }


    protected Scanner getReader () {
        return reader;
    }

    protected String getReponseUser () {
        return reponseUser;
    }

    protected void setReponseUser (String newResponse) {
        reponseUser = newResponse;
    }

    /**
     *
     * @return line from the user
     */
    protected String getNextLine(){
        // Display a prompt.
        System.out.print("> ");

        // Get the user input.
        return getReader().nextLine();
    }
}

