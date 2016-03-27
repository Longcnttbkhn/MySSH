package chpt.myssh.server.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.StringTokenizer;

import chpt.myssh.server.models.User;
import chpt.myssh.server.models.UserList;
import chpt.myssh.share.Command;

public abstract class ExecuteComand {
	protected String root_directory;
	protected String current_directory;

	public void setUserName(String userName) {
		root_directory = "home/" + userName;
		File rootUser = new File(root_directory);
		if (!rootUser.exists())
			rootUser.mkdirs();
		current_directory = "~";
	}

	public void login(String serverName) throws IOException, ClassNotFoundException {
		write("Input user name: ");
		String userName = read().getParameter(0);
		write("password: ");
		String password = read().getParameter(0);
		User user = new UserList().checkUser(userName, password);
		if (user != null) {
			writeln("Connect success!");
			setUserName(user.getUserName());
			while (true) {
				write(user.getUserName() + "@" + serverName + ":" + getCurrentPath() + "$ ");
				Command commandReceive = read();
				if (commandReceive.getCommandName().equals("logout")) {
					writeln("Goodbye");
					disconnect();
					break;
				} else {
					try {
						exe(commandReceive);
					} catch (IOException e) {
						writeln(e.getMessage());
					} catch (EmptyStackException e) {
						writeln("Can't access directory");
					}
				}
			}

		} else {
			writeln("Username or password is fail!");
			disconnect();
		}
	}

	public void exe(Command cmd) throws IOException, ClassNotFoundException {
		String cmdName = cmd.getCommandName();
		if (cmdName.equals("ls"))
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
		else if (cmdName.equals("cruser"))
			creatAccount();
		else
			writeln(cmdName);
	}

	public void creatAccount() throws IOException, ClassNotFoundException {
		write("Input user name: ");
		String userName = read().getParameter(0);
		UserList list = new UserList();
		if (!list.checkUser(userName)) {
			write("password: ");
			String password = read().getParameter(0);
			User user = new User(userName, password);
			list.save(user);
			writeln("Create user access");
		} else
			writeln("Duplicate user name");
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

	public abstract void disconnect() throws IOException;

	public abstract void write(String message) throws IOException;

	public abstract void writeln(String message) throws IOException;

	public abstract Command read() throws IOException, ClassNotFoundException;
}
