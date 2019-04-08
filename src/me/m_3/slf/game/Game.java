package me.m_3.slf.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.m_3.slf.Main;
import me.m_3.tiqoL.htmlbuilder.HTMLBody;
import me.m_3.tiqoL.htmlbuilder.HTMLDiv;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.HTMLSpan;
import me.m_3.tiqoL.htmlbuilder.box.HTMLBox;
import me.m_3.tiqoL.htmlbuilder.exceptions.UnknownObjectIDException;
import me.m_3.tiqoL.htmlbuilder.input.HTMLCheckbox;
import me.m_3.tiqoL.htmlbuilder.input.HTMLTextInput;
import me.m_3.tiqoL.htmlbuilder.input.TextInputType;
import me.m_3.tiqoL.user.User;
import me.m_3.tiqoL.user.UserStatus;

public class Game {
	
	Main main;
	UUID uuid;
	public User owner;
	public ArrayList<User> users;
	public ArrayList<String> categories;
	int rounds;
	int counter = 3;
	int game_timer_full = 60;
	int game_timer = 0;
	int remainingRounds;
	
	boolean dovoting;
	public boolean stop;
	
	public HashMap<User , Integer> scores = new HashMap<User , Integer>();
	public HashMap<User , Boolean> userready = new HashMap<User , Boolean>();
	public HashMap<User , HashMap<String , String>> answers = new HashMap<User , HashMap<String , String>>();
	public HashMap<User , HashMap<String , HashMap<User , Boolean>>> voting = new HashMap<User , HashMap<String , HashMap<User , Boolean>>>();
	
	public String currChar;
	
	Set<String> chars;
	
	@SuppressWarnings("unchecked")
	public Game(Main main , UUID uuid , User owner , ArrayList<User> users, ArrayList<String> categories , int rounds , Set<String> chars , int seconds , boolean voting , boolean stop) {
		this.chars = chars;
		this.main = main;
		this.uuid = uuid;
		this.owner = owner;
		this.users = (ArrayList<User>) users.clone();
		this.categories = categories;
		this.rounds = rounds;
		this.remainingRounds = rounds;
		this.dovoting = voting;
		this.stop = stop;
		
		this.game_timer_full = seconds;
		
		
		for (User user : users) {
			this.scores.put(user , 0);
		}
		
		showCountdown();
	}
	
	public void leaveUser(User user) {
		this.users.remove(user);
		if(main.userManager.getCurrentPage(user).equals("game_results")) {
			this.refreshResultOverview();
			Game game = this;
			
			boolean done = true;
			
			for (User u : game.users) {
				if (!game.userready.containsKey(u)) {
					done = false;
					continue;
				}
				if (!game.userready.get(u) && u.getUserStatus() == UserStatus.OPEN) {
					done = false;
				}
			}
			if (done) {
				game.nextGame();
			}
		}
		if (user.getUserStatus() != UserStatus.CLOSED)
			main.eventHandler.buildStartPage(user);
	}
	
	public void showCountdown() {
		this.counter = 3;
		userready = new HashMap<User , Boolean>();
		for (User user : users) {
			main.userManager.setCurrentPage(user, "game_countdown");
			HTMLBox box = new HTMLBox(this.main.getServer() , user);
			HTMLBody body = new HTMLBody();
			
			body.setJavaScriptCSS("font-family", "'M PLUS Rounded 1c', sans-serif");
			body.setJavaScriptCSS("backgroundImage", "url(\""+main.getServer().getContentServer().getURL("bg")+"\")");
			body.setJavaScriptCSS("backgroundRepeat", "repeat");
			body.setJavaScriptCSS("paddingLeft", "10%");
			body.setJavaScriptCSS("paddingRight", "10%");
			main.addTopbar(user, body);
			body.addChild(new HTMLObject("center").addChild(new HTMLObject("img").setHtmlAttribute("src", main.getServer().getContentServer().getURL("logo"))
					.setJavaScriptCSS("maxHeight", "10%")
					.setJavaScriptCSS("maxWidth", "80%")));
			
			body.addChild(new HTMLObject("br"));
			body.addChild(new HTMLObject("center").addChild(new HTMLObject("h1").setInnerText("3").setObjectID("slf.game_countdown.shutdownLobby")));
			
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
        			nextRound();
        		}
        		else {
        			scheduler.schedule(this, 1, TimeUnit.SECONDS);
        		}
			}
		};
		
		scheduler.schedule(task, 1, TimeUnit.SECONDS);
	}
	
	ScheduledExecutorService scheduler;
	
	public void nextRound() {
		
		game_timer = this.game_timer_full;
		answers = new HashMap<User , HashMap<String , String>>();
		
		for (User user : users) {
			
			HashMap<String , String> words = new HashMap<String , String>();
			for (String cat : this.categories) {
				words.put(cat , null);
			}
			answers.put(user, words);
		}
		
		Random generator = new Random();
		int randomIndex = generator.nextInt(new ArrayList<String>(this.chars).size());
		currChar = new ArrayList<String>(this.chars).get(randomIndex);
		
		for (User user : users) {
			main.userManager.setCurrentPage(user, "game_writing");
			HTMLBox box = new HTMLBox(this.main.getServer() , user);
			HTMLBody body = new HTMLBody();
			
			body.setJavaScriptCSS("font-family", "'M PLUS Rounded 1c', sans-serif");
			body.setJavaScriptCSS("backgroundImage", "url(\""+main.getServer().getContentServer().getURL("bg")+"\")");
			body.setJavaScriptCSS("backgroundRepeat", "repeat");
			body.setJavaScriptCSS("paddingLeft", "10%");
			body.setJavaScriptCSS("paddingRight", "10%");
			main.addTopbar(user, body);
			body.addChild(new HTMLObject("center").addChild(new HTMLObject("img").setHtmlAttribute("src", main.getServer().getContentServer().getURL("logo"))
					.setJavaScriptCSS("maxHeight", "10%")
					.setJavaScriptCSS("maxWidth", "80%")));
			
			HTMLDiv cardDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card");
			HTMLDiv cardContent = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-content").addChild(
					new HTMLSpan("Der aktuelle Buchstabe: <b>"+currChar+"</b>").setHtmlAttribute("class", "card-title"));
			
			for (String s : this.categories) {
				HTMLDiv div = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "input-field");
				div.addChild(new HTMLSpan(s));
				div.addChild(((HTMLTextInput) new HTMLTextInput(TextInputType.TEXT).setObjectID("slf.game_writing.entry."+s)).setTextInputHandler(main.getServer().getEventManager(), main.textInputHandler)
						.setHtmlAttribute("style", "background-image: linear-gradient(white , #ffecb3);"));
				cardContent.addChild(div);
				cardContent.addChild(new HTMLObject("br"));
			}
			
			HTMLDiv cardActionDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-action");
			
			HTMLDiv divloading = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "input-field").setHtmlAttribute("class", "progress");
			divloading.addChild(new HTMLDiv().setObjectID("slf.game_writing.progressBar").setHtmlAttribute("class", "determinate").setHtmlAttribute("style", "width:0%;"));
			cardActionDiv.addChild(divloading);
			
			cardDiv.addChild(cardContent);
			
			if (this.stop) {
				HTMLDiv div = (HTMLDiv) new HTMLDiv();
				div.setJavaScriptCSS("paddingLeft", "10%");
				div.setJavaScriptCSS("paddingRight", "10%");
				div.addChild(new HTMLObject("a").setObjectID("slf.game_writing.stop").setInnerText("STOPP!").setHtmlAttribute("href", "javascript:void(0)").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
						.setHtmlAttribute("class", "waves-effect waves-light btn-large red").setHtmlAttribute("style", "width:100%;"));
				cardActionDiv.addChild(div);
			}
			
			cardDiv.addChild(cardActionDiv);
			
			body.addChild(cardDiv);
			box.setHTMLBody(body);
			user.setHTMLBox(box);
		}
		
		//Start Thread Timer
		scheduler = Executors.newSingleThreadScheduledExecutor();
		final Game game = this;
		
		Runnable task = new Runnable() {
			public void run() {
				if (scheduler.isShutdown()) return;
				game_timer -= 1;
        		for (User user : game.users) {
        			try {
        				int percent = game_timer * 100 / game_timer_full;
						user.getHtmlBox().updateObject("slf.game_writing.progressBar", 
								new HTMLDiv().setObjectID("slf.game_writing.progressBar").setHtmlAttribute("class", "determinate").setHtmlAttribute("style", "width:" + (100-percent)+"%;")
								, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
        		if (game_timer == 0) {
        			showResults();
        		}
        		else {
        			scheduler.schedule(this, 1, TimeUnit.SECONDS);
        		}
			}
		};
		
		scheduler.schedule(task, 1, TimeUnit.SECONDS);
	}
	
	public void showResults() {
		scheduler.shutdown();
		
		//Fill Voting hashmap with true
		
		voting = new HashMap<User , HashMap<String , HashMap<User , Boolean>>>();
		for (User user1 : users) {
			
			HashMap<String , HashMap<User , Boolean>> categories = new HashMap<String , HashMap<User , Boolean>>();
			
			for (String s : this.categories) {
				HashMap<User , Boolean> votes = new HashMap<User , Boolean>();
				
				for (User user2 : users) {
					votes.put(user2, true);
				}
				
				categories.put(s, votes);
			}
			
			voting.put(user1, categories);
		}
		
		for (User user : users) {
			for (String cat : this.answers.get(user).keySet()) {
				String answer = this.answers.get(user).get(cat);
				if (answer == null) {
					for (User all : users) {
						voting.get(user).get(cat).put(all, false);
					}
				}
			}
		}
		
		for (User user : users) {
			HTMLBox box = new HTMLBox(this.main.getServer() , user);
			HTMLBody body = new HTMLBody();
			body.setJavaScriptCSS("font-family", "'M PLUS Rounded 1c', sans-serif");
			body.setJavaScriptCSS("backgroundImage", "url(\""+main.getServer().getContentServer().getURL("bg")+"\")");
			body.setJavaScriptCSS("backgroundRepeat", "repeat");
			body.setJavaScriptCSS("paddingLeft", "10%");
			body.setJavaScriptCSS("paddingRight", "10%");
			main.addTopbar(user, body);
			body.addChild(new HTMLObject("center").addChild(new HTMLObject("img").setHtmlAttribute("src", main.getServer().getContentServer().getURL("logo"))
					.setJavaScriptCSS("maxHeight", "10%")
					.setJavaScriptCSS("maxWidth", "80%")));
			
			HTMLDiv cardDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card");
			HTMLDiv cardContent = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-content").addChild(
					new HTMLSpan("Ergebnisse für den Buchstaben <b>"+currChar+"</b>").setHtmlAttribute("class", "card-title"));
			
			HTMLDiv divResults = (HTMLDiv) new HTMLDiv().setObjectID("slf.game_results.resultDiv");
			cardContent.addChild(divResults);
			
			HTMLDiv cardActionDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-action");
			
			HTMLDiv divBadges = (HTMLDiv) new HTMLDiv().setObjectID("slf.game_results.badgesDiv");
			cardActionDiv.addChild(divBadges);

			cardActionDiv.addChild(new HTMLObject("a").setObjectID("slf.game_results.accept").setInnerText("Passt so!").setHtmlAttribute("href", "javascript:void(0)").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
					.setHtmlAttribute("class", "waves-effect waves-light btn-large green").setHtmlAttribute("style", "width:100%;"));
			
			cardDiv.addChild(cardContent);
			cardDiv.addChild(cardActionDiv);
			body.addChild(cardDiv);
			box.setHTMLBody(body);
			user.setHTMLBox(box);
			main.userManager.setCurrentPage(user, "game_results");
		}
		
		refreshResultOverview();
		
		this.remainingRounds --;
		
	}
	
	public void refreshResultOverview() {
		for (User user : this.users) {
			
			HTMLDiv divBadges = (HTMLDiv) new HTMLDiv().setObjectID("slf.game_results.badgesDiv");;
			divBadges.setJavaScriptCSS("paddingLeft", "10%");
			divBadges.setJavaScriptCSS("paddingRight", "10%");
			
			for (User readyuser : users) {
				if (!this.userready.containsKey(readyuser)) {
					divBadges.addChild(new HTMLSpan("span").setHtmlAttribute("class", "new badge red").setHtmlAttribute("data-badge-caption", "").setInnerText(main.userManager.getUsername(readyuser)));
					continue;
				}
				if (!this.userready.get(readyuser)) {
					divBadges.addChild(new HTMLSpan("span").setHtmlAttribute("class", "new badge red").setHtmlAttribute("data-badge-caption", "").setInnerText(main.userManager.getUsername(readyuser)));
					continue;
				}
				divBadges.addChild(new HTMLSpan("span").setHtmlAttribute("class", "new badge green").setHtmlAttribute("data-badge-caption", "").setInnerText(main.userManager.getUsername(readyuser)));
			}
			
			try {
				user.getHtmlBox().updateObject("slf.game_results.badgesDiv", divBadges, false);
			} catch (UnknownObjectIDException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			HTMLDiv divResults = (HTMLDiv) new HTMLDiv().setObjectID("slf.game_results.resultDiv");
			for (String s : this.categories) {
				HTMLDiv card = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card");
				card.setJavaScriptCSS("paddingLeft", "3%");
				card.setJavaScriptCSS("paddingRight", "3%");
				HTMLDiv cardcontent = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-content");
				cardcontent.addChild(new HTMLSpan(s)).setHtmlAttribute("class", "card-title");
				
				//Table
				HTMLObject table = new HTMLObject("table").setHtmlAttribute("class", "striped");
				HTMLObject tablebody = new HTMLObject("tbody");
				for (User from : users) {
					HTMLObject tablerow = new HTMLObject("tr");
					
					tablerow.addChild(new HTMLObject("td").setInnerText(main.userManager.getUsername(from)).setHtmlAttribute("style", "width: 30%"));
					if (this.answers.get(from).get(s) == null) {
						tablerow.addChild(new HTMLObject("td").setInnerText("<i>...</i>"));
					}
					else {
						HTMLObject td = new HTMLObject("td").setHtmlAttribute("style", "width: 35%");
						HTMLObject label = new HTMLObject("label");
						
						if (this.dovoting) {
							label.addChild(((HTMLCheckbox) new HTMLCheckbox(this.voting.get(from).get(s).get(user)).setObjectID("slf.game_results.check."+s+"."+main.userManager.getUsername(from))).setCheckboxHandler(main.getServer().getEventManager() , main.checkboxHandler)
								.setHtmlAttribute("class", "filled-in"));
						}
						else if (user == owner) {
							label.addChild(((HTMLCheckbox) new HTMLCheckbox(this.voting.get(from).get(s).get(user)).setObjectID("slf.game_results.check."+s+"."+main.userManager.getUsername(from))).setCheckboxHandler(main.getServer().getEventManager() , main.checkboxHandler)
									.setHtmlAttribute("class", "filled-in"));
						}
						
						if (dovoting) {
							if (getVotes(from , s) > users.size() / 2)
								label.addChild(new HTMLSpan("<b>" + this.answers.get(from).get(s) + "</b>").setHtmlAttribute("style", "color:black;"));
							else {
								label.addChild(new HTMLSpan("<strike>" + this.answers.get(from).get(s) + "</strike>").setHtmlAttribute("style", "color:black;"));
							}
						}
						else {
							boolean voted = this.voting.get(from).get(s).get(owner);
							if (voted)
								label.addChild(new HTMLSpan("<b>" + this.answers.get(from).get(s) + "</b>").setHtmlAttribute("style", "color:black;"));
							else {
								label.addChild(new HTMLSpan("<strike>" + this.answers.get(from).get(s) + "</strike>").setHtmlAttribute("style", "color:black;"));
							}
						}
												
						td.addChild(label);
						tablerow.addChild(td);
					}
					HTMLObject td = new HTMLObject("td").setHtmlAttribute("style", "width: 35%").setHtmlAttribute("align", "right");

					if (this.dovoting) {
						for (User voted : users) {
							if (this.voting.get(from).get(s).get(voted)) {
								td.addChild(new HTMLObject("i").setHtmlAttribute("class", "material-icons Medium green-text").setInnerText("check"));
							}
							else {
								td.addChild(new HTMLObject("i").setHtmlAttribute("class", "material-icons Medium red-text").setInnerText("clear"));
							}
						}
					}
					else {
						if (this.voting.get(from).get(s).get(owner)) {
							td.addChild(new HTMLObject("i").setHtmlAttribute("class", "material-icons Medium green-text").setInnerText("check"));
						}
						else {
							td.addChild(new HTMLObject("i").setHtmlAttribute("class", "material-icons Medium red-text").setInnerText("clear"));
						}
					}
					
					
					tablerow.addChild(td);
					
					tablebody.addChild(tablerow);
				}
				table.addChild(tablebody);
				cardcontent.addChild(table);
				
				card.addChild(cardcontent);
				divResults.addChild(card);
			}
			try {
				user.getHtmlBox().updateObject("slf.game_results.resultDiv", divResults, false);
			} catch (UnknownObjectIDException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int getVotes (User user , String cat) {
		HashMap<User, Boolean> votes = this.voting.get(user).get(cat);
		int i = 0;
		for (boolean b : votes.values()) {
			if (b) i ++;
		}
		return i;
	}
	
	public void nextGame() {
		for (String category : this.categories) {
			
			HashMap<User , String> answered = new HashMap<User , String>();
			for (User user : users) {
				if (dovoting) {
					if (getVotes(user , category) > users.size() / 2) {
						answered.put(user , this.answers.get(user).get(category));
					}
				}
				else {
					boolean voted = this.voting.get(user).get(category).get(owner);
					if (voted) {
						answered.put(user , this.answers.get(user).get(category));
					}
				}
			}
			
			if (answered.size() == 0) continue;
			if (answered.size() == 1) {
				User won = new ArrayList<User>(answered.keySet()).get(0);
				this.scores.put(won , this.scores.get(won) + 20);
			}
			else {
				for (User key : answered.keySet()) {
					String answer = answered.get(key);
					int i = 0;
					for (String s : answered.values()) {
						if (s.equalsIgnoreCase(answer)) i ++;
					}
					if (i == 1) {
						this.scores.put(key , this.scores.get(key) + 10);
					}
					else {
						this.scores.put(key , this.scores.get(key) + 5);
					}
				}
			}
			
		}
		if (this.remainingRounds == 0) {
			showEndScreen();
		}
		else {
			showCountdown();
		}
	}
	
	public void showEndScreen() {
		for (User user : users) {
			
			@SuppressWarnings("unchecked")
			HashMap<User,Integer> scores = (HashMap<User, Integer>) this.scores.clone();
			
			HTMLBox box = new HTMLBox(this.main.getServer() , user);
			HTMLBody body = new HTMLBody();
			body.setJavaScriptCSS("font-family", "'M PLUS Rounded 1c', sans-serif");
			body.setJavaScriptCSS("backgroundImage", "url(\""+main.getServer().getContentServer().getURL("bg")+"\")");
			body.setJavaScriptCSS("backgroundRepeat", "repeat");
			body.setJavaScriptCSS("paddingLeft", "10%");
			body.setJavaScriptCSS("paddingRight", "10%");
			main.addTopbar(user, body);
			body.addChild(new HTMLObject("center").addChild(new HTMLObject("img").setHtmlAttribute("src", main.getServer().getContentServer().getURL("logo"))
					.setJavaScriptCSS("maxHeight", "10%")
					.setJavaScriptCSS("maxWidth", "80%")));
			
			HTMLDiv cardDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card");
			HTMLDiv cardContent = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-content").addChild(
					new HTMLSpan("<b>ENDE</b>").setHtmlAttribute("class", "card-title"));
			
			HTMLDiv divResults = (HTMLDiv) new HTMLDiv().setObjectID("slf.game_results.resultDiv");
			
			HTMLObject collection = new HTMLObject("ul").setHtmlAttribute("class", "collection");
			divResults.addChild(collection);
			
			int rank = 1;
			
			for (@SuppressWarnings("unused") User subuser : users) {
				User highest = new ArrayList<User>(scores.keySet()).get(0);
				for (User x : users) {
					if (scores.get(x) == null || scores.get(highest) == null) continue;
					if (scores.get(x) > scores.get(highest)) {
						highest = x;
					}
				}
				
				HTMLObject userEntry = new HTMLObject("li").setHtmlAttribute("class", "collection-item");
				userEntry.setInnerText(rank + " - " + main.userManager.getUsername(highest));
				userEntry.addChild(new HTMLSpan("span").setHtmlAttribute("class", "new badge red").setHtmlAttribute("data-badge-caption", "").setInnerText("" + this.scores.get(highest)));
				collection.addChild(userEntry);
				
				scores.remove(highest);
				rank ++;
			}
			
			cardContent.addChild(divResults);
			
			HTMLDiv cardActionDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-action");
			
			cardActionDiv.addChild(new HTMLObject("a").setObjectID("slf.game_end.leave").setInnerText("Verlassen").setHtmlAttribute("href", "javascript:void(0)").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
					.setHtmlAttribute("class", "waves-effect waves-light btn-large yellow black-text").setHtmlAttribute("style", "width:100%;"));
			
			cardDiv.addChild(cardContent);
			cardDiv.addChild(cardActionDiv);
			body.addChild(cardDiv);
			box.setHTMLBody(body);
			user.setHTMLBox(box);
			main.userManager.setCurrentPage(user, "game_end");
		}
	}
	
	public void shutdown() {
		main.gameManager.unregister(this);
		for (User user : users) {
			main.eventHandler.buildStartPage(user);
		}
	}
}
