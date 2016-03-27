package chpt.myssh.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Command implements Serializable {

	private ArrayList<String> parameters;

	public Command(String commandLine) {
		parameters = new ArrayList<String>();
		if (commandLine.contains(" ")) {
			StringTokenizer strToken = new StringTokenizer(commandLine, " ");
			while (strToken.hasMoreTokens()) {
				parameters.add(strToken.nextToken());
			}
		} else
			parameters.add(commandLine);
	}

	public String getCommandName() {
		return parameters.get(0);
	}

	public String getParameter(int number) {
		if (number >= parameters.size())
			return null;
		else
			return parameters.get(number);
	}

	public void addParameter(String parameter) {
		parameters.add(parameter);
	}

	public boolean hasParameters() {
		return (parameters.size() > 1);
	}
}
