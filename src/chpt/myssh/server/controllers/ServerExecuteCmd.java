package chpt.myssh.server.controllers;

import java.io.IOException;
import java.util.Scanner;

import chpt.myssh.share.Command;

public class ServerExecuteCmd extends ExecuteComand {
	private Scanner scanner;
	private Server server;

	public ServerExecuteCmd(Scanner scanner, Server server) {
		// TODO Auto-generated constructor stub
		this.scanner = scanner;
		this.server = server;
	}

	@Override
	public void exe(Command cmd) throws IOException, ClassNotFoundException {
		if (cmd.getCommandName().equals("ssh")) {
			String option = cmd.getParameter(1);
			if (option == null)
				writeln("What do you want?\n" + "Start ssh service: ssh start\n" + "Test ssh service: ssh status\n"
						+ "Stop ssh service: ssh stop");
			else if (option.equals("start")) {
				if (server.getSShThread() == null)
					startSSH();
				else {
					if (server.getSShThread().getCurrentClient() > 0)
						System.out.println("SSh has been running");
					else 
						startSSH();
				}
			} else if (option.equals("status")) {
				if (server.getSShThread() == null)
					System.out.println("SSh is not running");
				else
					System.out.println(server.getSShThread().getStatus());
			} else if (option.equals("stop")) {
				if (server.getSShThread() == null)
					System.out.println("SSh is not running");
				else {
					server.getSShThread().stopSSh();
					System.out.println("SSh was stopped");
				}
			}
		} else
			super.exe(cmd);
	}

	public void startSSH() throws IOException {
		System.out.print("Input max client connect: ");
		SShThread ssh = new SShThread(scanner.nextInt(), server.getServerName());
		scanner.nextLine();
		server.setSShThread(ssh);
		new Thread(ssh).start();
		System.out.println("Server run in port: 8888");
		
	}

	@Override
	public void write(String message) throws IOException {
		// TODO Auto-generated method stub
		System.out.print(message);
	}

	@Override
	public void writeln(String message) throws IOException {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public Command read() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Command cmd = new Command(scanner.nextLine());
		return cmd;
	}

	@Override
	public void disconnect() throws IOException {
		// TODO Auto-generated method stub

	}

}
