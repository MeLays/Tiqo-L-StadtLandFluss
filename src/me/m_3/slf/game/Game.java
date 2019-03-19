package me.m_3.slf.game;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.m_3.slf.Main;
import me.m_3.tiqoL.htmlbuilder.HTMLBody;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.box.HTMLBox;
import me.m_3.tiqoL.user.User;

public class Game {
	
	Main main;
	UUID uuid;
	User owner;
	ArrayList<User> users;
	ArrayList<String> categories;
	int rounds;
	int counter = 5;
	int remainingRounds;
	
	@SuppressWarnings("unchecked")
	public Game(Main main , UUID uuid , User owner , ArrayList<User> users, ArrayList<String> categories , int rounds) {
		this.main = main;
		this.uuid = uuid;
		this.owner = owner;
		this.users = (ArrayList<User>) users.clone();
		this.categories = categories;
		this.rounds = rounds;
		this.remainingRounds = rounds;
		
		for (User user : users) {
			main.userManager.setCurrentPage(user, "game_countdown");
		}
		showCountdown();
	}
	
	public void showCountdown() {
		for (User user : users) {
			HTMLBox box = new HTMLBox(this.main.getServer() , user);
			HTMLBody body = new HTMLBody();
			body.addChild(new HTMLObject("br"));
			body.addChild(new HTMLObject("center").addChild(new HTMLObject("h1").setInnerText("5").setObjectID("slf.game_countdown.shutdownLobby")));
			
			box.setHTMLBody(body);
			user.setHTMLBox(box);
		}
		
		System.out.println(users);
		
		//Start Thread Timer
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		final Game game = this;
		
		Runnable task = new Runnable() {
			public void run() {
				counter -= 1;
        		for (User user : game.users) {
        			try {
						user.getHtmlBox().updateObject("slf.game_countdown.shutdownLobby", new HTMLObject("h1").setInnerText(counter+"").setObjectID("slf.game_countdown.shutdownLobby"), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
        		if (counter == 0) {
        			scheduler.shutdown();
        			//Show game screen
        		}
        		else {
        			scheduler.schedule(this, 1, TimeUnit.SECONDS);
        		}
			}
		};
		
		scheduler.schedule(task, 1, TimeUnit.SECONDS);
	}

}
