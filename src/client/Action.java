package client;

/**
 * Created by tom on 21/05/15.
 */
public abstract class Action {

	private final String name;

	public Action(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract void execute();
}
