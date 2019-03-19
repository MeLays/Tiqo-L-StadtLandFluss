package me.m_3.slf.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.m_3.slf.Main;
import me.m_3.tiqoL.htmlbuilder.HTMLBody;
import me.m_3.tiqoL.htmlbuilder.HTMLDiv;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.HTMLSpan;
import me.m_3.tiqoL.htmlbuilder.box.HTMLBox;
import me.m_3.tiqoL.htmlbuilder.exceptions.UnknownObjectIDException;
import me.m_3.tiqoL.user.User;
import me.m_3.tiqoL.user.UserStatus;

public class Lobby {
	
	Main  main;
	User owner;
	ArrayList<User> users = new ArrayList<User>();
	UUID uuid;
	
	String[] categories = {"Stadt" , "Land" , "Fluss" , "Name" , "Tier" , "Beruf"
			, "Obst/Gemüse" , "Getränk" , "Pizzazutat" , "Pflanze" , "Film/Serie" , "YouTuber" , "Pornotitel" , "Buch" , "Etwas Peinliches" , "Superkraft",
			"Promi" , "Fußballspieler" , "Politiker" , "Körperteil" , "Sprache" , "Hobby" , "Gefühl" , "Krankheit",
			"Berg/Gebirge" , "Hauptstadt" , "Insel" , "Computerspiel" , "Computerspiel Charakter" , "Jugendwort",
			"Chemisches Element" , "Englisches Wort" , "Verb" , "Schulfach"};
	
	Set<String> enabledCategories = new HashSet<String>();
	
	public Lobby(Main main , UUID uuid) {
		this.main = main;
		this.uuid = uuid;
		
		//Choose Random Categories
		ArrayList<String> random = new ArrayList<String>(Arrays.asList(categories));
		Collections.shuffle(random);
		enabledCategories.add(random.get(0));
		enabledCategories.add(random.get(1));
		enabledCategories.add(random.get(2));
		enabledCategories.add(random.get(3));
		enabledCategories.add(random.get(4));
	}
	
	public void setOwner(User user) {
		owner = user;
		if (!users.contains(user))
			joinUser(user);
	}
	
	public void joinUser(User user) {
		users.add(user);
		setupUser(user);
		main.userManager.setCurrentPage(user, "lobby");
		updateUserList();
		updateCategories(user);
	}
	
	public void leaveUser(User user) {
		users.remove(user);
		updateUserList();
		if (user == owner || users.size() == 0) {
			shutdown();
		}
		if (user.getUserStatus() == UserStatus.OPEN) {
			main.eventHandler.buildStartPage(user);
		}
	}
	
	public void kickUser(User remove) {
		leaveUser(remove);
		remove.alert("Du wurdest aus dem Spiel geworfen!");
		main.eventHandler.buildStartPage(remove);
	}
	
	@SuppressWarnings("unchecked")
	public void shutdown() {
		for (User user : (ArrayList<User>) this.users.clone()) {
			main.eventHandler.buildStartPage(user);
			user.alert("Die Lobby in der du warst wurde geschlossen!");
			users.remove(user);
		}
		main.lobbyManager.unregisterLobby(uuid);
	}
	
	public boolean isOwner(User user) {
		return (user == owner);
	}
	
	public void setupUser(User user) {
		HTMLBox box = new HTMLBox(main.getServer() , user);
		
		HTMLBody body = new HTMLBody();
		body.setJavaScriptCSS("font-family", "'M PLUS Rounded 1c', sans-serif");
		body.addChild(new HTMLSpan("<h4>Willkommen in der Lobby " + uuid+"</h4>"));
		
		body.addChild(new HTMLObject("hr"));
		
		HTMLDiv categoriesDiv = (HTMLDiv) new HTMLDiv().setObjectID("slf.lobby.categoriesDiv");
		body.addChild(categoriesDiv);
		
		body.addChild(new HTMLObject("hr"));
		body.addChild(new HTMLObject("h4").setInnerText("Spieler:"));
		
		HTMLDiv userListDiv = (HTMLDiv) new HTMLDiv().setObjectID("slf.lobby.userListDiv");
		body.addChild(userListDiv);
		body.addChild(new HTMLObject("hr"));
		
		if (user == owner) {
			body.addChild(new HTMLObject("a").setObjectID("slf.lobby.shutdownLobby").setInnerText("Close Lobby").setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
					.setHtmlAttribute("class", "waves-effect waves-teal btn-flat"));
			body.addChild(new HTMLObject("a").setObjectID("slf.lobby.startLobby").setInnerText("Start").setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
					.setHtmlAttribute("class", "btn waves-effect waves-light green accent-3"));
		}
		body.addChild(new HTMLObject("a").setObjectID("slf.lobby.leaveLobby").setInnerText("Verlassen").setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
				.setHtmlAttribute("class", "waves-effect waves-teal btn-flat"));
		
		box.setHTMLBody(body);
		user.setHTMLBox(box);
	}
	
	@SuppressWarnings("unchecked")
	public void startGame() {
		if (users.size() < 2) return;
		ArrayList<String> cats = new ArrayList<String>();
		cats.addAll(this.enabledCategories);
		main.gameManager.createGame(owner, users, cats, 5);
		for (User user : (ArrayList<User>) this.users.clone()) {
			users.remove(user);
		}
		main.lobbyManager.unregisterLobby(uuid);
	}
	
	public void updateUserList() {
		for (User user : this.users) {
			HTMLDiv userListDiv = (HTMLDiv) new HTMLDiv().setObjectID("slf.lobby.userListDiv");
			HTMLObject collection = new HTMLObject("ul").setHtmlAttribute("class", "collection");
			userListDiv.addChild(collection);
			
			for (User subuser : users) {
				HTMLObject userEntry = new HTMLObject("li").setHtmlAttribute("class", "collection-item");
				userEntry.setInnerText(main.userManager.getUsername(subuser) + "&nbsp;&nbsp;");
				if (this.isOwner(subuser)) {
					userEntry.setInnerText("<b>" + main.userManager.getUsername(subuser) + "</b>&nbsp;&nbsp;");
				}
				if (this.isOwner(user) && user != subuser) {
					userEntry.addChild(new HTMLObject("a").setObjectID("slf.lobby.removeUser."+main.userManager.getUsername(subuser)).setInnerText("Remove").setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
							.setHtmlAttribute("class", "btn waves-effect waves-light red lighten-2"));
				}
				else if (this.isOwner(user) && user == subuser) {
					userEntry.addChild(new HTMLObject("a").setObjectID("slf.lobby.removeUser."+main.userManager.getUsername(subuser)).setInnerText("Remove").setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
							.setHtmlAttribute("class", "btn disabled"));
				}
				collection.addChild(userEntry);
			}
					
			try {
				user.getHtmlBox().updateObject("slf.lobby.userListDiv", userListDiv, false);
			} catch (UnknownObjectIDException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toggleCategory(String cat) {
		if (Arrays.asList(this.categories).contains(cat)) {
			if (this.enabledCategories.contains(cat) && this.enabledCategories.size() > 1) {
				this.enabledCategories.remove(cat);
			}
			else {
				this.enabledCategories.add(cat);
			}
		}
		this.updateCategories();
	}
	
	public void updateCategories() {
		for (User user : this.users) {
			updateCategories(user);
		}
	}
	
	public void updateCategories(User user) {
		HTMLDiv categoryListDiv = (HTMLDiv) new HTMLDiv().setObjectID("slf.lobby.categoriesDiv");
		for (String s : this.categories) {
			if (this.enabledCategories.contains(s))
				categoryListDiv.addChild(new HTMLObject("a").setObjectID("slf.lobby.toggleCategory."+s).setInnerText(s).setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
					.setHtmlAttribute("class", "btn waves-effect waves-light"));
			else {
				categoryListDiv.addChild(new HTMLObject("a").setObjectID("slf.lobby.toggleCategory."+s).setInnerText(s).setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
						.setHtmlAttribute("class", "waves-effect waves-teal btn-flat"));
			}
		}
		try {
			user.getHtmlBox().updateObject("slf.lobby.categoriesDiv", categoryListDiv, false);
		} catch (UnknownObjectIDException e) {
			e.printStackTrace();
		}
	}

}
