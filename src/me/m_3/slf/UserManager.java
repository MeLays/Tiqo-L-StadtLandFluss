package me.m_3.slf;

import java.util.HashMap;
import java.util.Random;

import me.m_3.tiqoL.user.User;

public class UserManager {

	Main main;

	HashMap<User,String> usernames = new HashMap<User,String>();
	HashMap<User,String> currentPage = new HashMap<User,String>();
	
	String[] preNames = {"Rot", "Blau" , "Grün" , "Wald" , "Nacht" , "Klein" , "Groß" , "Meer" , "Wasser"};
	String[] names = {"Katze" , "Kater" , "Tiger" , "Hund" , "Aal" , "Seegurke" , "Echse" , "Fisch" , "Kabeljau" , "Vogel" , "Delfin" , "Hamster" , "Taube" , "Pferd" , "Kuh" , "Schwein"};
	
	public UserManager(Main main) {
		this.main = main;
	}
	
	public void registerUser(User user) {
		Random generator = new Random();
		String username = preNames[generator.nextInt(preNames.length)] + names[generator.nextInt(names.length)];
		usernames.put(user, fitUsername(username));
	}
	
	public void unregisterUser(User user) {
		if (usernames.containsKey(user)) usernames.remove(user);
		if (currentPage.containsKey(user)) currentPage.remove(user);
	}
	
	public String getUsername(User user) {
		return usernames.get(user);
	}
	
	public boolean setUsername(User user , String username) {
		if (username.length() > 32) return false;
		for (String s : usernames.values()) {
			if (s.equals(username)) {
				return false;
			}
		}
		usernames.put(user , username);
		return true;
	}
	
	public String getCurrentPage(User user) {
		return currentPage.get(user);
	}
	
	public void setCurrentPage(User user , String page) {
		currentPage.put(user , page);
	}
	
	public String fitUsername(String username) {
		for (String s : usernames.values()) {
			if (s.equals(username)) {
				return fitUsername(username + "1");
			}
		}
		return username;
	}

	public User getUser(String username) {
		for (User user : usernames.keySet()) {
			if (usernames.get(user).equals(username)) return user;
		}
		return null;
	}
}
