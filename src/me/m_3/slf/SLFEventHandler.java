package me.m_3.slf;

import java.util.UUID;

import me.m_3.slf.game.Lobby;
import me.m_3.tiqoL.event.EventHandler;
import me.m_3.tiqoL.htmlbuilder.HTMLBody;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.HTMLSpan;
import me.m_3.tiqoL.htmlbuilder.box.HTMLBox;
import me.m_3.tiqoL.htmlbuilder.exceptions.UnknownObjectIDException;
import me.m_3.tiqoL.htmlbuilder.input.HTMLTextInput;
import me.m_3.tiqoL.htmlbuilder.input.TextInputType;
import me.m_3.tiqoL.user.User;

public class SLFEventHandler implements EventHandler{
	
	Main main;
	public SLFEventHandler(Main main) {
		this.main = main;
	}
	

	public void onHandshakeComplete(User user , String secret) {
		
		//New user connects ...
		main.userManager.registerUser(user);
		
		user.setTitle("Stadt Land Fluss");
		
		user.addHeaderTag("<link href=\"https://fonts.googleapis.com/css?family=M+PLUS+Rounded+1c\" rel=\"stylesheet\">");
		user.addHeaderTag("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0-rc.2/css/materialize.min.css\">");
		
		if (user.getParameters().has("game")) {
			try {
				UUID uuid = UUID.fromString(user.getParameters().getString("game"));
				Lobby join = main.lobbyManager.getLobby(uuid);
				join.joinUser(user);
				return;
			} catch (Exception e) {
				user.alert("Das Spiel dem du beitreten wolltest existiert nicht!");
			}
		}
		buildStartPage(user);
		
	}
	
	public void onConnectionEnd(User user , int code , String reason , boolean remote) {
		Lobby lobby = main.lobbyManager.findUser(user);
		if (lobby != null) {
			lobby.leaveUser(user);
		}
		main.userManager.unregisterUser(user);
		this.updateStartPage();
	}
	
	//Update Methods
	
	public void buildStartPage(User user) {
		HTMLBox box = new HTMLBox(main.getServer() , user);
		
		HTMLBody body = new HTMLBody();
		body.setJavaScriptCSS("font-family", "'M PLUS Rounded 1c', sans-serif");
		body.addChild(new HTMLSpan("Willkommen bei Stadt Land Fluss, "+main.userManager.getUsername(user)+"!<br>"
				+ "Momentan sind " + main.getServer().getUserMap().size() + " Nutzer online ...").setObjectID("slf.start.welcomeMessage"));
		body.addChild(new HTMLObject("br"));
		body.addChild(((HTMLTextInput) new HTMLTextInput(TextInputType.TEXT).setObjectID("slf.start.textInputUsername")).setTextInputHandler(main.getServer().getEventManager(), main.textInputHandler).
				setHtmlAttribute("placeholder" , "Username").setHtmlAttribute("value", main.userManager.getUsername(user)));
		body.addChild(new HTMLObject("hr"));
		
		body.addChild(new HTMLObject("a").setObjectID("slf.start.createGame").setInnerText("Create Game").setHtmlAttribute("href", "#").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
				.setHtmlAttribute("class", "waves-effect waves-light btn"));
		
		box.setHTMLBody(body);
		user.setHTMLBox(box);
		main.userManager.setCurrentPage(user, "start");
		
		this.updateStartPage();
	}
	
	public void updateStartPage() {
		for (User user : main.getServer().getUserMap().values()) {
			if (!main.userManager.getCurrentPage(user).equals("start"))
				continue;
			updateStartPage(user);
		}
	}
	public void updateStartPage(User user) {
		try {
			user.getHtmlBox().updateObject("slf.start.welcomeMessage", 
					user.getHtmlBox().getDirectAccess().get("slf.start.welcomeMessage").setInnerText("Willkommen bei Stadt Land Fluss, "+main.userManager.getUsername(user)+"!<br>"
			+ "Momentan sind " + main.getServer().getUserMap().size() + " Nutzer online ...")
					, true);
		} catch (UnknownObjectIDException e) {
			e.printStackTrace();
		}
	}
	
	
}
