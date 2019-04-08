package me.m_3.slf;

import java.io.File;

import me.m_3.slf.game.Game;
import me.m_3.slf.game.GameManager;
import me.m_3.slf.game.LobbyManager;
import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.contentserver.ServableFile;
import me.m_3.tiqoL.coreloader.Core;
import me.m_3.tiqoL.htmlbuilder.HTMLBody;
import me.m_3.tiqoL.htmlbuilder.HTMLDiv;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.user.User;

public class Main extends Core{
	
	public ServableFile background;
	public ServableFile logo;

	public SLFEventHandler eventHandler;
	public SLFTextInputHandler textInputHandler;
	public SLFClickHandler clickHandler;
	public SLFCheckboxHandler checkboxHandler;
	
	public UserManager userManager;
	
	public LobbyManager lobbyManager;
	
	public GameManager gameManager;
	
	public Main(WSServer server, String name) {
		super(server, name);
		
		this.userManager = new UserManager(this);
		this.textInputHandler = new SLFTextInputHandler(this);
		this.clickHandler = new SLFClickHandler(this);
		this.checkboxHandler = new SLFCheckboxHandler(this);
		
		this.lobbyManager = new LobbyManager(this);
		this.gameManager = new GameManager(this);
				
		Logger.info("[SLf] Registering EventHandler ....");
		eventHandler = new SLFEventHandler(this);
		this.registerEventHandler(eventHandler);
		
    	File file = new File(System.getProperty("user.dir") + File.separator + "core" + File.separator + "bg.png");
    	this.background = new ServableFile(file);
    	this.getServer().getContentServer().serveFile(this.background, "bg");
    	
    	file = new File(System.getProperty("user.dir") + File.separator + "core" + File.separator + "logo.png");
    	this.logo = new ServableFile(file);
    	this.getServer().getContentServer().serveFile(this.logo, "logo");
		
	}
	
	public void addTopbar(User user , HTMLBody body) {
		body.addChild(new HTMLObject("br").setHtmlAttribute("style", "line-height: 51px;"));
		HTMLDiv topbar = new HTMLDiv();
		topbar.setHtmlAttribute("style", "left:0 ; width: 100%; position: fixed; background-color: #ffecb3; height: 51px; top: 0px; z-index: 999;").setHtmlAttribute("class", "z-depth-2");
		
		if (this.userManager.getCurrentPage(user).equals("start")) {
			addButton(topbar, "Start" , true , "slf.topbar.start.startbutton");
		}
		if (this.userManager.getCurrentPage(user).startsWith("lobby")) {
			addButton(topbar, this.userManager.getUsername(user) , true , "slf.topbar.start.lobbyButton");
			addButton(topbar, "Verlassen" , false , "slf.topbar.start.leaveButton");
		}
		if (this.userManager.getCurrentPage(user).startsWith("game")) {
			Game game = this.gameManager.findUser(user);
			if (game == null) {
				addButton(topbar, this.userManager.getUsername(user) , true , "slf.topbar.start.gameButton");
				addButton(topbar, "Verlassen" , false , "slf.topbar.start.leaveButton");				
				body.addChild(topbar);
				return;
			}
			addButton(topbar, this.userManager.getUsername(user) , true , "slf.topbar.start.gameButton");
			addButton(topbar, game.scores.get(user) + " Punkte" , true , "slf.topbar.start.pointsButton");
			addButton(topbar, "Verlassen" , false , "slf.topbar.start.leaveButton");
		}
		
		body.addChild(topbar);
	}
	
	public void addButton(HTMLDiv topbar , String text , boolean active, String id) {
		HTMLObject buttonDiv = new HTMLDiv().setHtmlAttribute("style", "position: relative; top: 13px; float:left;");
		HTMLObject button = new HTMLObject("a").setInnerText(text);
		button.setObjectID(id);
		if (active)
			button.setHtmlAttribute("style", "" +
					"  background-color: #ffc107;" + 
					"  color: black;" +
					"  text-align: center;" + 
					"  padding: 14px 16px;"+
					"  height: 50px;" +
					"  font-size: 17px;");
		else {
			button.setHtmlAttribute("style", "" +
					"  background-color: #ffecb3 ;" + 
					"  color: #ff6f00;" +
					"  text-align: center;" + 
					"  padding: 14px 16px;"+
					"  height: 50px;" +
					"  font-size: 17px;");
			button.setHtmlAttribute("href", "javascript:void(0)");
			button.setClickHandler(this.getServer().getEventManager(), this.clickHandler);
		}
		buttonDiv.addChild(button);
		topbar.addChild(buttonDiv);
	}

}
