package chpt.myssh.server.controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SShThread implements Runnable {
	private int max_client;
	private int current_client;
	private String serverName;
	private boolean sshOn;

	public SShThread(int max_client, String serverName) {
		this.max_client = max_client;
		this.serverName = serverName;
		this.sshOn = true;
	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(8888);
			while (sshOn) {
				try  {
					Socket socket = serverSocket.accept();
					Thread thread = new Thread(new ClientThread(socket, this, serverName));
					thread.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
				}
			}
			serverSocket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		}
		
	}

	public String getStatus() {
		StringBuilder status = new StringBuilder();
		if (sshOn)
			status.append("SSh is running\n");
		else
			status.append("SSh is stoping\n");
		status.append("Max client connect: " + max_client + "\n");
		status.append("Current client connected: " + current_client);
		return status.toString();
	}
	
	public int getCurrentClient() {
		return current_client;
	}
	
	public void stopSSh() {
		sshOn = false;
	}

	public synchronized void connect_client() {
		while (current_client >= max_client) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		current_client++;
		notify();
	}

	public synchronized void disconnect_client() {
		while (current_client <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		current_client--;
		notify();
	}

	public boolean isOverload() {
		return (current_client >= max_client);
	}

}
