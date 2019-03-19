package me.m_3.slf;

import me.m_3.tiqoL.htmlbuilder.handlers.HTMLTextInputHandler;
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
		}
	}
		
}
