package chpt.myssh.server.controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	private int current_client;
	private int max_client;
	private String server_name;

	public Server() {
		current_client = 0;
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
	
	public boolean isOverload() {
		return (current_client >= max_client);
	}

	public String getServerName() {
		return server_name;
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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		Server server = new Server();
		System.out.print("Input server name: ");
		server.server_name = scanner.nextLine();
		System.out.print("Input max client connect: ");
		server.max_client = scanner.nextInt();
		scanner.close();
		ServerSocket serverSocket = new ServerSocket(8888);
		System.out.println("Server run in port: 8888");
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				Thread thread = new Thread(new SShThread(socket, server));
				thread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				
			}

		}
	}

}
