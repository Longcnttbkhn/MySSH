package chpt.myssh.server.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class UserList {
	private ArrayList<User> listUser;
	private String fileName = "user_pass.txt";
	
	public UserList() {
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
			String line;
			listUser = new ArrayList<User>();
			while ((line = reader.readLine()) != null) {
				StringTokenizer strToken = new StringTokenizer(line, "|");
				User user = new User(strToken.nextToken(), strToken.nextToken());
				listUser.add(user);
			}
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}
	
	public User checkUser(String userName, String password) {
		User user = null;
		for (User i : listUser) {
			if (i.getUserName().equals(userName)) {
				if (i.getPassword().equals(password)) {
					user = i;
					break;
				}
			}
		}
		return user;
	}
	
	public boolean checkUser(String userName) {
		boolean hasUser = false;
		for (User i : listUser) {
			if (i.getUserName().equals(userName)) {
				hasUser = true;
				break;
			}
		}
		return hasUser;
	}
	
	public void save(User user) {
		try (PrintWriter write = new PrintWriter(new FileWriter(fileName, true));) {
			write.print("\n" + user.toString());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}	
