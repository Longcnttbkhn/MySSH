package chpt.myssh.server.controllers;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;
import java.util.StringTokenizer;

import chpt.myssh.server.models.Command;

public class ExecuteComand {
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String root_directory;
	private String current_directory;

	public ExecuteComand(ObjectInputStream input, ObjectOutputStream output) {
		this.input = input;
		this.output = output;
	}

	public void setUserName(String userName) {
		root_directory = "home/" + userName;
		current_directory = "~";
	}

	public void exe(Command cmd) throws IOException {
		String cmdName = cmd.getCommandName();
		if (cmdName.equals("ls"))
			ls(cmd.getParameter(1));
		else if (cmdName.equals("cd"))
			cd(cmd.getParameter(1));
		else if (cmdName.equals("pwd"))
			pwd();
		else
			writeln(cmdName);
	}

	public void pwd() throws IOException {
		writeln(getFullPath());
	}

	public void cd(String path) throws IOException {
		String userPath = getUserPath(path);
		String fullPath = root_directory + "/" + userPath;
		File file = new File(fullPath);
		if (file.isDirectory()) {
			current_directory = "~/" + userPath;
		} else
			writeln("No such directory");
	}

	public String getUserPath(String path) {
		String userPath = current_directory + "/" + path;
		Stack<String> stackPath = new Stack<String>();
		StringTokenizer strToken = new StringTokenizer(userPath, "/");
		while (strToken.hasMoreTokens()) {
			String node = strToken.nextToken();
			if (node.equals("..")) {
				stackPath.pop();
			} else if (node.equals("~")) {
				stackPath.removeAllElements();
			} else if (node.equals(".")) {

			} else {
				stackPath.push(node);
			}
		}
		StringBuilder stringBuilder = new StringBuilder();
		if (stackPath.isEmpty()) {
			userPath = null;
		} else {
			for (String i : stackPath) {
				stringBuilder.append(i + "/");
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		userPath = stringBuilder.toString();
		return userPath;
	}

	public void ls(String path) throws IOException {
		String fullPath = null;
		if (path == null) 
			fullPath = getFullPath();
		else { 
			String userPath = getUserPath(path);
			fullPath = root_directory + "/" + userPath;
		}
		File file = new File(fullPath);
		if (file.isDirectory()) {
			File[] lsFile = file.listFiles();
			StringBuilder result = new StringBuilder();
			for (File i : lsFile) {
				if (i.isDirectory()) {
					result.append(String.format(" %10s   %-10s   %-10s\n", "Directory", i.getName(), ""));
				} else if (i.isFile()) {
					result.append(String.format(" %10s   %-10s   %-10s\n", "file", i.getName(), getSizeFile(i.length())));
				}
			}
			write(result.toString());
		} else 
			writeln("No such directory");
	}

	public String getSizeFile(long length) {
		String size = null;
		if ((length / 1024l) == 0) {
			size = String.format("%d Byte", length);
		} else {
			length = length / 1024l;
			if ((length / 1024l) == 0) {
				size = String.format("%d KB", length);
			} else {
				length = length / 1024l;
				size = String.format("%d MB", length);
			}
		}
		return size;
	}

	public String getFullPath() {
		return root_directory + current_directory.substring(1);
	}

	public String getCurrentPath() {
		return current_directory;
	}

	public void write(String message) throws IOException {
		Command command = new Command("write");
		command.addParameter(message);
		output.writeObject(command);
		output.flush();
	}

	public void writeln(String message) throws IOException {
		Command command = new Command("writeln");
		command.addParameter(message);
		output.writeObject(command);
		output.flush();
	}

	public void disconnect() throws IOException {
		Command command = new Command("logout");
		output.writeObject(command);
		output.flush();
	}

	public Command read() throws IOException, ClassNotFoundException {
		Command commandSend = new Command("read");
		output.writeObject(commandSend);
		output.flush();

		Command commandReceive = (Command) input.readObject();
		return commandReceive;
	}
}
