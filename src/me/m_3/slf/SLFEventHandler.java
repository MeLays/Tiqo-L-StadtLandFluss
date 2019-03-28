package me.m_3.slf;

import java.util.HashMap;
import java.util.UUID;

import me.m_3.slf.game.Game;
import me.m_3.slf.game.Lobby;
import me.m_3.tiqoL.event.EventHandler;
import me.m_3.tiqoL.htmlbuilder.HTMLBody;
import me.m_3.tiqoL.htmlbuilder.HTMLDiv;
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
	
	HashMap<User , String> fastJoin = new HashMap<User , String>();

	public void onHandshakeComplete(User user , String secret) {
		
		//New user connects ...
		main.userManager.registerUser(user);
		
		user.setTitle("Stadt Land Fluss");
		
		user.addHeaderTag("<link href=\"https://fonts.googleapis.com/css?family=M+PLUS+Rounded+1c\" rel=\"stylesheet\">");
		user.addHeaderTag("<link href=\"https://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">");
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
		if (user.getParameters().has("join")) {
			try {
				fastJoin.put(user, user.getParameters().getString("join"));
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
		
		Game game = main.gameManager.findUser(user);
		if (game != null) {
			game.leaveUser(user);
		}
		
		main.userManager.unregisterUser(user);
		this.updateStartPage();
	}
	
	//Update Methods
	
	public void buildStartPage(User user) {
		main.userManager.setCurrentPage(user, "start");
		HTMLBox box = new HTMLBox(main.getServer() , user);
		
		HTMLBody body = new HTMLBody();
		body.setJavaScriptCSS("font-family", "'M PLUS Rounded 1c', sans-serif");
		body.setJavaScriptCSS("backgroundImage", "url(\""+main.getServer().getContentServer().getURL("bg")+"\")");
		body.setJavaScriptCSS("backgroundRepeat", "repeat");
		body.setJavaScriptCSS("paddingLeft", "10%");
		body.setJavaScriptCSS("paddingRight", "10%");
		
		main.addTopbar(user, body);
		
		body.addChild(new HTMLObject("center").addChild(new HTMLObject("img").setHtmlAttribute("src", main.getServer().getContentServer().getURL("logo"))
				.setJavaScriptCSS("maxHeight", "10%")
				.setJavaScriptCSS("maxWidth", "80%")
				.setJavaScriptCSS("top", "50px")));
		
		HTMLDiv cardDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card");
		HTMLDiv cardContent = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-content").addChild(
				new HTMLSpan("Willkommen bei Stadt Land Fluss, <b>"+main.userManager.getUsername(user)+"</b>!").setHtmlAttribute("class", "card-title").setObjectID("slf.start.usernameSpan"));
		
		cardContent.addChild(new HTMLSpan("Momentan sind " + main.getServer().getUserMap().size() + " Nutzer online ...").setObjectID("slf.start.welcomeMessage"));
		
		cardContent.addChild(new HTMLSpan("<br>Dein Benutzername: "));
		
		HTMLDiv inlineForm = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "input-field inline");
		
		
		inlineForm.addChild(((HTMLTextInput) new HTMLTextInput(TextInputType.TEXT).setObjectID("slf.start.textInputUsername")).setTextInputHandler(main.getServer().getEventManager(), main.textInputHandler).
				setHtmlAttribute("placeholder" , "Username").setHtmlAttribute("value", main.userManager.getUsername(user))
				.setHtmlAttribute("style", "background-image: linear-gradient(white , #ffecb3);"));
		
		cardContent.addChild(inlineForm);

		HTMLDiv cardActionDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-action");
		
		cardActionDiv.addChild(new HTMLObject("a").setObjectID("slf.start.createGame").setInnerText("Spiel erstellen").setHtmlAttribute("href", "javascript:void(0)").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
				.setHtmlAttribute("class", "waves-effect waves-light btn amber lighten-5 black-text"));
		
		
		HTMLDiv cardJoinDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-action");
		
		cardJoinDiv.addChild(new HTMLSpan("Lobby Code: "));
		
		String lobbyCode = "";
		if (this.fastJoin.containsKey(user)) {
			lobbyCode = fastJoin.get(user);
		}
		
		cardJoinDiv.addChild(((HTMLTextInput) new HTMLTextInput(TextInputType.TEXT).setObjectID("slf.start.textInputJoinCode")).setTextInputHandler(main.getServer().getEventManager(), main.textInputHandler)
				.setHtmlAttribute("style", "background-image: linear-gradient(white , #ffecb3); width:30%; font: 20px consolas")
				.setJavaScriptCSS("minWidth", "100px")
				.setHtmlAttribute("value", lobbyCode));

		
		cardJoinDiv.addChild(new HTMLObject("a").setObjectID("slf.start.joinGame").setInnerText("Spiel beitreten").setHtmlAttribute("href", "javascript:void(0)").setClickHandler(main.getServer().getEventManager(), main.clickHandler)
				.setHtmlAttribute("class", "waves-effect waves-light btn amber lighten-5 black-text"));
		
		cardDiv.addChild(cardContent);
		cardDiv.addChild(cardActionDiv);
		cardDiv.addChild(cardJoinDiv);
		body.addChild(cardDiv);
		box.setHTMLBody(body);
		user.setHTMLBox(box);
		
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
			user.getHtmlBox().updateObject("slf.start.usernameSpan",
					user.getHtmlBox().getDirectAccess().get("slf.start.usernameSpan").setInnerText("Willkommen bei Stadt Land Fluss, <b>"+main.userManager.getUsername(user)+"</b>!")
					, true);
			user.getHtmlBox().updateObject("slf.start.welcomeMessage", 
					user.getHtmlBox().getDirectAccess().get("slf.start.welcomeMessage").setInnerText("Momentan sind <b>" + main.getServer().getUserMap().size() + "</b> Nutzer online.")
					, true);
		} catch (UnknownObjectIDException e) {
			e.printStackTrace();
		}
	}
	
	
}
