package chpt.myssh.clients.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

import chpt.myssh.server.models.Command;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);

		boolean tieptuc = true;
		while (tieptuc) {
			System.out.print("myShell~$ ");
			String cmd = scanner.nextLine();
			if (cmd.equals("exit")) {
				tieptuc = false;
			} else {
				StringTokenizer strToken = new StringTokenizer(cmd, " ");
				if (strToken.nextToken().equals("ssh")) {
					String ip = strToken.nextToken();
					try (Socket socket = new Socket(InetAddress.getByAddress(getIp(ip)), 8888);
							ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
							ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
						
						boolean tieptuc1 = true;
						while (tieptuc1) {
							Command commandReceive = (Command) input.readObject();
							if (commandReceive.getCommandName().equals("write")) 
								System.out.print(commandReceive.getParameter(1));
							else if (commandReceive.getCommandName().equals("writeln"))
								System.out.println(commandReceive.getParameter(1));
							else if (commandReceive.getCommandName().equals("read")) {
								Command comandSend = new Command(scanner.nextLine());
								output.writeObject(comandSend);
								output.flush();
							} else if (commandReceive.getCommandName().equals("logout"))
								tieptuc1 = false;

						}
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						System.out.println("Can't connect to server");
					}
				}
			}
		}
		scanner.close();
	}

	public static byte[] getIp(String ip) {
		StringTokenizer strToken = new StringTokenizer(ip, ".");
		byte[] ipByte = new byte[4];
		for (int i = 0; i < 4; i++) {
			ipByte[i] = Byte.valueOf(strToken.nextToken());
		}
		return ipByte;
	}
}
