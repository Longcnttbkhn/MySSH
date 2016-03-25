package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static BufferedReader input;
	public static PrintWriter output;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket = new ServerSocket(8888);
		System.out.println("Server run");
		Socket server = serverSocket.accept();
		input = new BufferedReader(new InputStreamReader(server.getInputStream()));
		output = new PrintWriter(server.getOutputStream());
		System.out.println("Server accept");
		boolean tiepTuc = true;
		
		while (tiepTuc) {
			write("long@hust~$ ");
			String cmd = read();
			if (cmd.equals("logout") || cmd.equals("exit")) {
				disconnect();
				tiepTuc = false;
			}
			write("Server da nhan lenh " + cmd);
		}
		
		input.close();
		output.close();
		server.close();
		serverSocket.close();
	}
	
	public static void write(String str) {
		output.println(str);
		output.flush();
	}
	
	public static String read() throws IOException {
		String str = null;
		output.println("read");
		output.flush();
		str = input.readLine();
		return str;
	}
	
	public static void disconnect() {
		output.println("logout");
		output.flush();
	}

}
