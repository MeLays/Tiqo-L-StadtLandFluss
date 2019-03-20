package me.m_3.slf;

import me.m_3.slf.game.Game;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLCheckboxHandler;
import me.m_3.tiqoL.user.User;

public class SLFCheckboxHandler implements HTMLCheckboxHandler{
	
	Main main;
	
	public SLFCheckboxHandler(Main main) {
		this.main = main;
	}

	public void onToggle(User user , String htmlObject , boolean checked) {
		if (main.userManager.getCurrentPage(user).equals("game_results")) {
			if(htmlObject.startsWith("slf.game_results.check.")) {
				Game game = main.gameManager.findUser(user);
				if (game == null) return;
				String text = htmlObject.replace("slf.game_results.check.", "");
				String cat = text.split("\\.")[0];
				String username = text.split("\\.")[1];
				try {
					User voteduser = main.userManager.getUser(username);
					game.voting.get(voteduser).get(cat).put(user , checked);
					game.refreshResultOverview();
				}
				catch(Exception ex) {
					
				}
			}
		}
	}
}
