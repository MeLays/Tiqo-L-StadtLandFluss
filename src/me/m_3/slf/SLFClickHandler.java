package me.m_3.slf;

import me.m_3.slf.game.Game;
import me.m_3.slf.game.Lobby;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.HTMLSpan;
import me.m_3.tiqoL.htmlbuilder.exceptions.UnknownObjectIDException;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLClickHandler;
import me.m_3.tiqoL.user.User;
import me.m_3.tiqoL.user.UserStatus;

public class SLFClickHandler implements HTMLClickHandler{

	Main main;
	
	public SLFClickHandler (Main main) {
		this.main = main;
	}
	
	public void onClick(User user , String htmlObject , double x , double y , double pageX , double pageY) {
		if (htmlObject.equals("slf.topbar.start.leaveButton")) {
			Lobby currentLobby = main.lobbyManager.findUser(user);
			if (currentLobby != null) {
				currentLobby.leaveUser(user);
				return;
			}
			
			Game game = main.gameManager.findUser(user);
			if (game != null) {
				game.leaveUser(user);
			}
		}
		if (main.userManager.getCurrentPage(user).equals("start")) {
			if(htmlObject.equals("slf.start.createGame")) {
				main.lobbyManager.createLobby(user);
			}
			if(htmlObject.equals("slf.start.joinGame")) {
				Lobby lobby = main.lobbyManager.getLobby(main.lobbyManager.fastJoin.get(main.eventHandler.fastJoin.get(user)));
				if (lobby == null) {
					user.alert("Der Lobby Code ist leider ungültig!");
					return;
				}
				lobby.joinUser(user);
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
			else if (htmlObject.startsWith("slf.lobby.toggleChar.")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				String cat = htmlObject.replace("slf.lobby.toggleChar.", "");
				currentLobby.toggleChar(cat);
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
			else if (htmlObject.equals("slf.lobby.settingsRoundsMinus")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				
				if (currentLobby.rounds > 1)
					currentLobby.rounds --;
				
				currentLobby.updateSettings();
			}
			else if (htmlObject.equals("slf.lobby.settingsRoundsPlus")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				
				if (currentLobby.rounds < 10)
					currentLobby.rounds ++;
				
				currentLobby.updateSettings();
			}
			else if (htmlObject.equals("slf.lobby.settingsTimeMinus")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				
				if (currentLobby.seconds > 10)
					currentLobby.seconds -= 10;
				
				currentLobby.updateSettings();
			}
			else if (htmlObject.equals("slf.lobby.settingsTimePlus")) {
				Lobby currentLobby = main.lobbyManager.findUser(user);
				if (currentLobby == null) return;
				if (!currentLobby.isOwner(user)) return;
				
				if (currentLobby.seconds < 240)
					currentLobby.seconds += 10;
				
				currentLobby.updateSettings();
			}
			
		}
		if (main.userManager.getCurrentPage(user).equals("game_writing")) {
			if(htmlObject.equals("slf.game_writing.stop")) {
				Game game = main.gameManager.findUser(user);
				if (game == null) return;
				if (!game.stop) return;
				game.showResults();
			}

		}		
		
		if (main.userManager.getCurrentPage(user).equals("game_results")) {
			if(htmlObject.equals("slf.game_results.accept")) {
				Game game = main.gameManager.findUser(user);
				if (game == null) return;
				game.userready.put(user , true);
				game.refreshResultOverview();
				try {
					user.getHtmlBox().updateObject("slf.game_results.accept", new HTMLObject("center").addChild(new HTMLSpan("<br>Warte auf die anderen Spieler ...")), false);
				} catch (UnknownObjectIDException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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

		}
		
		if (main.userManager.getCurrentPage(user).equals("game_end")) {
			if(htmlObject.equals("slf.game_end.leave")) {
				Game game = main.gameManager.findUser(user);
				if (game == null) return;
				game.leaveUser(user);
			}

		}
	}
	
}
