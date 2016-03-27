 package chpt.myssh.server.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import chpt.myssh.server.models.User;
import chpt.myssh.share.Command;

public class Server {
	
	private String server_name;
	private SShThread ssh;
	
	public String getServerName() {
		return server_name;
	}
	
	public SShThread getSShThread() {
		return ssh;
	}
	
	public void setSShThread(SShThread ssh) {
		this.ssh = ssh;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		Server server = new Server();
		File serverInfo = new File("server_info.txt");
		if (serverInfo.exists()) {
			BufferedReader read = new BufferedReader(new FileReader(serverInfo));;
			server.server_name = read.readLine();
			read.close();
		} else {
			System.out.print("Input server name: ");
			server.server_name = scanner.nextLine();
			PrintWriter write = new PrintWriter(serverInfo);
			write.print(server.server_name);
			write.close();
		}
		File user_pass = new File("user_pass.txt");
		if (!user_pass.exists()) {
			System.out.println("Create account");
			System.out.print("Input user name: ");
			String userName = scanner.nextLine();
			System.out.print("password: ");
			String password = scanner.nextLine();
			User user = new User(userName, password);
			PrintWriter write = new PrintWriter(user_pass);
			write.print(user.toString());
			write.close();
		}
		boolean tieptuc = true;
		while (tieptuc) {
			System.out.print("Guest@" + server.server_name + ":~$ ");
			Command cmd = new Command(scanner.nextLine());
			if (cmd.getCommandName().equals("exit")) {
				tieptuc = false;
				if (server.ssh != null)
					server.ssh.stopSSh();
			}
			else if (cmd.getCommandName().equals("login")) {
				ServerExecuteCmd exeCmd = new ServerExecuteCmd(scanner, server);
				try {
					exeCmd.login(server.server_name);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
				}
			} else 
				System.out.println("Not found command");
		}
	}

}
