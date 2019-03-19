package me.m_3.slf;

import me.m_3.slf.game.Lobby;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLClickHandler;
import me.m_3.tiqoL.user.User;

public class SLFClickHandler implements HTMLClickHandler{

	Main main;
	
	public SLFClickHandler (Main main) {
		this.main = main;
	}
	
	public void onClick(User user , String htmlObject , double x , double y , double pageX , double pageY) {
		if (main.userManager.getCurrentPage(user).equals("start")) {
			if(htmlObject.equals("slf.start.createGame")) {
				main.lobbyManager.createLobby(user);
			}
		}
		if (main.userManager.getCurrentPage(user).equals("lobby")) {
			if(htmlObject.equals("slf.lobby.shutdownLobby")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				currentLobby.shutdown();
			}
			else if (htmlObject.startsWith("slf.lobby.removeUser.")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				String username = htmlObject.replace("slf.lobby.removeUser.", "");
				User remove = main.userManager.getUser(username);
				if (remove == null) return;
				currentLobby.kickUser(remove);
			}
			else if (htmlObject.startsWith("slf.lobby.toggleCategory.")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				String cat = htmlObject.replace("slf.lobby.toggleCategory.", "");
				currentLobby.toggleCategory(cat);
			}
			else if (htmlObject.equals("slf.lobby.leaveLobby")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				currentLobby.leaveUser(user);
			}
			else if (htmlObject.equals("slf.lobby.startLobby")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				currentLobby.startGame();
			}
		}
	}
	
}
