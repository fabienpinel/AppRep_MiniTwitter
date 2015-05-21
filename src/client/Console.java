package client;

import java.util.Scanner;

/**
 * Created by Fabien on 09/05/15.
 */
public class Console {
    protected Scanner reader;
    protected String reponseUser;

    public Console () {
        this.reader = new Scanner(System.in);
        this.reponseUser = "";
    }


    public void sayHello(){
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
    public String getNextLine(){
		return getNextLine(null);
    }

	public String getNextLine(String defaultValue) {
		// Display a prompt.
		System.out.print("> ");
		if (defaultValue != null) {
			System.out.print("[default="+defaultValue+"]");
		}

		// Get the user input.
		String input = getReader().nextLine();
		if (input.length() > 0) return input;
		return defaultValue;
	}
}

