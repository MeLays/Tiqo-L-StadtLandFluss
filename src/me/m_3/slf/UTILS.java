package me.m_3.slf;

import me.m_3.tiqoL.htmlbuilder.HTMLDiv;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.HTMLSpan;

public class UTILS {
	
	public static HTMLDiv createCard(String title , HTMLObject body , HTMLObject action) {
		HTMLDiv cardDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card");
		HTMLDiv cardContent = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-content").addChild(
				new HTMLSpan(title).setHtmlAttribute("class", "card-title"));
		
		cardContent.addChild(body);
		
		cardDiv.addChild(cardContent);
		
		if(action != null) {
			HTMLDiv cardActionDiv = (HTMLDiv) new HTMLDiv().setHtmlAttribute("class", "card-action");
			cardActionDiv.addChild(action);
			cardDiv.addChild(cardActionDiv);
		}
		
		return cardDiv;
	}

}
