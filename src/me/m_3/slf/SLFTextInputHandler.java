package me.m_3.slf;

import java.util.HashMap;

import me.m_3.slf.game.Game;
import me.m_3.tiqoL.htmlbuilder.exceptions.UnknownObjectIDException;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLTextInputHandler;
import me.m_3.tiqoL.htmlbuilder.input.HTMLTextInput;
import me.m_3.tiqoL.user.User;

public class SLFTextInputHandler implements HTMLTextInputHandler{

	Main main;
	
	public SLFTextInputHandler (Main main) {
		this.main = main;
	}
	
	public void onInput(User user , String htmlObject , String text) {
		if (main.userManager.getCurrentPage(user).equals("start")) {
			if(htmlObject.equals("slf.start.textInputUsername")) {
				text = text.replaceAll("[^a-zA-Z]", "");
				if (text.equals("")) return;
				if (text.length() < 3) return;
				if (main.userManager.setUsername(user, text)) {
					main.eventHandler.updateStartPage(user);
				}
			}
			else if (htmlObject.equals("slf.start.textInputJoinCode")) {
				main.eventHandler.fastJoin.put(user , text);
			}
		}
		else if (main.userManager.getCurrentPage(user).equals("game_writing")) {
			if(htmlObject.startsWith("slf.game_writing.entry.")) {
				Game game = main.gameManager.findUser(user);
				if (game == null) return;
				text = text.replaceAll("[^a-zA-Z0-9 ]", "");
				String category = htmlObject.replace("slf.game_writing.entry.", "");
				if (!game.categories.contains(category)) return;
				if (text.length() > 64) return;
				if (!text.toUpperCase().startsWith(game.currChar)) {
					HTMLTextInput textInput;
					try {
						textInput = (HTMLTextInput) user.getHtmlBox().getObject(htmlObject);
						textInput.setHtmlAttribute("value", "");
						user.getHtmlBox().updateObject(htmlObject, textInput, true);
					} catch (UnknownObjectIDException e) {

					}				}
				HashMap<String , String> answers = game.answers.get(user);
				answers.put(category, text);
				game.answers.put(user, answers);			
			}
		}
	}
		
}
