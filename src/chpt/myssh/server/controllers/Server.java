package chpt.myssh.server.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import chpt.myssh.clients.controllers.MyThread;

public class Server {
	private int current_client;
	private int max_client;

	public Server(int max_client) {
		this.max_client = max_client;
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
	
	public int getCurrentClient() {
		return current_client;
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
		
		Server server = new Server(2);
		ServerSocket serverSocket = new ServerSocket(8888);
		System.out.println("Server running");
		while (true) {
			Socket socket = serverSocket.accept();
			server.connect_client();
			System.out.println("Connect to client " + server.current_client);
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							PrintWriter output = new PrintWriter(socket.getOutputStream())){
						boolean tieptuc = true;
						while (tieptuc) {
							String str = input.readLine();
							output.println("user" + server.current_client + ": " + str);
							output.flush();
							if (str.equals("exit")) 
								tieptuc = false;
						}
						socket.close();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					server.disconnect_client();
				}
			});
			t.start();
		}

	}

}
