package chpt.myssh.server.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread implements Runnable {
	private Socket socket;
	private SShThread sshThread;
	private String serverName;

	public ClientThread(Socket socket, SShThread sshThread, String serverName) throws IOException {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.sshThread = sshThread;
		this.serverName = serverName;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
			RemoteExecuteCmd exeCmd = new RemoteExecuteCmd(input, output);
			if (sshThread.isOverload()) {
				exeCmd.writeln("Overload server");
				exeCmd.disconnect();
			} else {
				sshThread.connect_client();
				exeCmd.login(serverName);
				sshThread.disconnect_client();
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			sshThread.disconnect_client();
		}
	}
}
