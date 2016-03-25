package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Socket client = new Socket("localhost", 8888);
		System.out.println("Client connected to server");
		Scanner scanner = new Scanner(System.in);
		BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
		PrintWriter output = new PrintWriter(client.getOutputStream());
	
		boolean tiepTuc = true;
		while (tiepTuc) {
			String command = input.readLine();
			if (command.equals("logout")) 
				tiepTuc = false;
			else if (command.equals("read")) {
				command = scanner.nextLine();
				output.println(command);
				output.flush();
			} else {
				System.out.print(command);
			}
		}
		scanner.close();
		input.close();
		output.close();
		client.close();
	}

}
