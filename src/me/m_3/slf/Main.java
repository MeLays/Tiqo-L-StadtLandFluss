package me.m_3.slf;

import java.io.File;

import me.m_3.slf.game.GameManager;
import me.m_3.slf.game.LobbyManager;
import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.contentserver.ServableFile;
import me.m_3.tiqoL.coreloader.Core;

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

}
