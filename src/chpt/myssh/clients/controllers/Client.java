package chpt.myssh.clients.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

import chpt.myssh.share.Command;
import chpt.myssh.share.Package;

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
							String cmdName = commandReceive.getCommandName();
							if (cmdName.equals("write"))
								System.out.print(commandReceive.getParameter(1));
							else if (cmdName.equals("writeln"))
								System.out.println(commandReceive.getParameter(1));
							else if (cmdName.equals("read")) {
								Command comandSend = new Command(scanner.nextLine());
								output.writeObject(comandSend);
								output.flush();
							} else if (cmdName.equals("createfile")) {
								File file = new File(commandReceive.getParameter(1));
								OutputStream write = new FileOutputStream(file);
								boolean tieptuc2 = true;
								do {
									Package pack = (Package) input.readObject();
									int length = pack.getLength();
									if (length > 0)
										write.write(pack.getBuffer(), 0, length);
									else
										tieptuc2 = false;
								} while (tieptuc2);
								write.close();
							} else if (cmdName.equals("logout"))
								tieptuc1 = false;

						}

					} catch (ConnectException e) {
						System.out.println(e.getMessage());
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						System.out.println(e);
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
