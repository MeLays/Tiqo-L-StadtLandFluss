package me.m_3.slf;

import me.m_3.slf.game.GameManager;
import me.m_3.slf.game.LobbyManager;
import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.coreloader.Core;

public class Main extends Core{

	public SLFEventHandler eventHandler;
	SLFTextInputHandler textInputHandler;
	public SLFClickHandler clickHandler;
	
	public UserManager userManager;
	
	public LobbyManager lobbyManager;
	
	public GameManager gameManager;
	
	public Main(WSServer server, String name) {
		super(server, name);
		
		this.userManager = new UserManager(this);
		this.textInputHandler = new SLFTextInputHandler(this);
		this.clickHandler = new SLFClickHandler(this);
		
		this.lobbyManager = new LobbyManager(this);
		this.gameManager = new GameManager(this);
				
		Logger.info("[SLf] Registering EventHandler ....");
		eventHandler = new SLFEventHandler(this);
		this.registerEventHandler(eventHandler);
		
		
	}

}
