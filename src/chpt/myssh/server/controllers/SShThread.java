package chpt.myssh.server.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EmptyStackException;

import chpt.myssh.share.Command;
import chpt.myssh.server.models.User;
import chpt.myssh.server.models.UserList;

public class SShThread implements Runnable {
	private Socket socket;
	private Server server;

	public SShThread(Socket socket, Server server) throws IOException {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
			ExecuteComand exeCmd = new ExecuteComand(input, output);
			if (server.isOverload()) {
				exeCmd.writeln("Overload server");
				exeCmd.disconnect();
			} else {
				server.connect_client();
				exeCmd.write("Input user name: ");
				String userName = exeCmd.read().getParameter(0);
				exeCmd.write("password: ");
				String password = exeCmd.read().getParameter(0);
				User user = new UserList().checkUser(userName, password);
				if (user != null) {
					exeCmd.writeln("Connect success!");
					exeCmd.setUserName(user.getUserName());
					while (true) {
						exeCmd.write(user.getUserName() + "@" + server.getServerName() + ":" + exeCmd.getCurrentPath()
								+ "$ ");
						Command commandReceive = exeCmd.read();
						try {
							exeCmd.exe(commandReceive);
						} catch (IOException e) {
							exeCmd.writeln(e.getMessage());
						} catch (EmptyStackException e) {
							exeCmd.writeln("Can't access directory");
						}
					}

				} else
					exeCmd.writeln("Username or password is fail!");
				server.disconnect_client();
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
			server.disconnect_client();
		}
	}
}
