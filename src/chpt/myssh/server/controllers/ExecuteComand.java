package chpt.myssh.server.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.StringTokenizer;

import chpt.myssh.share.Command;
import chpt.myssh.share.Package;

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
		if (cmdName.equals("logout"))
			logout();
		else if (cmdName.equals("ls"))
			ls(cmd.getParameter(1));
		else if (cmdName.equals("cd"))
			cd(cmd.getParameter(1));
		else if (cmdName.equals("pwd"))
			pwd();
		else if (cmdName.equals("mkdir")) {
			if (cmd.getParameter(1) == null)
				writeln("No path of directory");
			else
				mkdir(cmd.getParameter(1));
		} else if (cmdName.equals("rm")) {
			if (cmd.getParameter(1) == null)
				writeln("No path of file or directory");
			else
				rm(cmd.getParameter(1));
		} else if (cmdName.equals("cp")) {
			if (cmd.getParameter(2) == null)
				writeln("Not enough parameter");
			else
				cp(cmd.getParameter(1), cmd.getParameter(2));
		} else if (cmdName.equals("mv")) {
			if (cmd.getParameter(2) == null)
				writeln("Not enough parameter");
			else
				mv(cmd.getParameter(1), cmd.getParameter(2));
		} else if (cmdName.equals("echo"))
			echo(cmd.getParameter(1), cmd.getParameter(2));
		else if (cmdName.equals("time"))
			time();
		else if (cmdName.equals("get")) {
			if (cmd.getParameter(1) == null)
				writeln("No file name");
			else
				get(cmd.getParameter(1));
		} else
			writeln(cmdName);
	}

	public void get(String path) throws IOException {
		String fullPath = root_directory + "/" + getUserPath(path);
		File fileRead = new File(fullPath);
		if (fileRead.exists()) {
			writeln("Downloading ... ");
			get(fileRead);
			writeln("Download commplete");
		}
	}

	public void get(File file) throws IOException {
		if (file.isFile()) {
			createFile(file.getName());
			InputStream read = new FileInputStream(file);
			int length;
			byte[] buffer = new byte[1024];
			while ((length = read.read(buffer)) > 0) {
				output.writeObject(new Package(length, buffer));
				output.reset();
				output.flush();
			}
			output.writeObject(new Package(-1, null));
			output.reset();
			output.flush();
			read.close();
		}
	}

	public void createFile(String fileName) throws IOException {
		Command cmd = new Command("createfile");
		cmd.addParameter(fileName);
		output.writeObject(cmd);
		output.flush();
	}

	public void time() throws IOException {
		Date today = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
		String s = format.format(today.getTime());
		writeln(s);
	}

	public void echo(String arg1, String path) throws IOException {
		if (arg1 == null)
			writeln("");
		else if (path == null) {
			writeln(arg1);
		} else {
			String fullPath = root_directory + "/" + getUserPath(path);
			File fileOutput = new File(fullPath);
			fileOutput.createNewFile();
			PrintWriter print = new PrintWriter(new FileOutputStream(fileOutput));
			print.print(arg1);
			print.close();
			writeln(arg1);
		}
	}

	public void mv(String arg1, String arg2) throws IOException {
		String fullPath1 = root_directory + "/" + getUserPath(arg1);
		String fullPath2 = root_directory + "/" + getUserPath(arg2);
		File file1 = new File(fullPath1);
		File file2 = new File(fullPath2);
		if (file1.exists()) {
			cp(file1, file2);
			rm(file1);
		} else
			writeln("No such file or directory");
	}

	public void rm(String path) throws IOException {
		String fullPath = root_directory + "/" + getUserPath(path);
		File file = new File(fullPath);
		if (file.exists()) {
			rm(file);
			writeln("Delete success");
		} else
			writeln("No such file or directory");
	}

	public void rm(File file) {
		if (file.isFile())
			file.delete();
		else if (file.isDirectory()) {
			for (File i : file.listFiles()) {
				rm(i);
			}
			file.delete();
		}
	}

	public void mkdir(String path) throws IOException {
		String fullPath = root_directory + "/" + getUserPath(path);
		File file = new File(fullPath);
		if (file.isDirectory()) {
			writeln("This directory is exists");
		} else {
			file.mkdirs();
			writeln("Create directory success");
		}
	}

	public void cp(String arg1, String arg2) throws IOException {
		String fullPath1 = root_directory + "/" + getUserPath(arg1);
		String fullPath2 = root_directory + "/" + getUserPath(arg2);
		File file1 = new File(fullPath1);
		File file2 = new File(fullPath2);
		if (file1.exists()) {
			cp(file1, file2);
		} else
			writeln("No such file or directory");
	}

	public void cp(File fileInput, File fileOutput) throws IOException {
		if (fileInput.isFile()) {
			if (fileInput.equals(fileOutput)) {
				writeln("Duplicate file names");
			}
			if (fileOutput.isDirectory()) {
				File file = new File(fileOutput, fileInput.getName());
				cp(fileInput, file);
			} else {
				InputStream in = new FileInputStream(fileInput);
				OutputStream out = new FileOutputStream(fileOutput);
				byte[] buffer = new byte[1024];
				int length = 0;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();
			}
		} else if (fileInput.isDirectory()) {
			fileOutput.mkdirs();
			for (File i : fileInput.listFiles()) {
				File fileDich = new File(fileOutput, i.getName());
				cp(i, fileDich);
			}
		}
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
					result.append(
							String.format(" %10s   %-10s   %-10s\n", "file", i.getName(), getSizeFile(i.length())));
				}
			}
			write(result.toString());
		} else
			writeln("No such directory");
	}

	public void logout() throws IOException {
		writeln("Good buy");
		disconnect();
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
