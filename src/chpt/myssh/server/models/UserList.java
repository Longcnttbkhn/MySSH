package chpt.myssh.server.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
			e.printStackTrace();
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
}	
