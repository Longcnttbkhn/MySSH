package chpt.myssh.clients.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyThread implements Runnable {
	private Socket socket;
	private int stt;

	public MyThread(Socket socket, int stt) throws IOException {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.stt = stt;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
			boolean tieptuc = true;
			while (tieptuc) {
				String str = input.readLine();
				System.out.println("user" + stt + ": " + str);
				if (str.equals("exit")) 
					tieptuc = false;
			}
			socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
