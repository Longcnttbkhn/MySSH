package chpt.myssh.clients.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Socket socket = new Socket("localhost", 8888);
		Scanner scanner = new Scanner(System.in);
		try (PrintWriter output = new PrintWriter(socket.getOutputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			boolean tieptuc = true;
			while (tieptuc) {
				System.out.print("myShell$ ");
				String command = scanner.nextLine();
				output.println(command);
				output.flush();
				System.out.println(input.readLine());
				if (command.equals("exit")) 
					tieptuc = false;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		socket.close();
		scanner.close();
	}

}
