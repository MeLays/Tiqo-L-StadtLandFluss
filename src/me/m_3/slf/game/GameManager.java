package me.m_3.slf.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import me.m_3.slf.Main;
import me.m_3.tiqoL.user.User;

public class GameManager {
	
	Main main;
	
	HashMap<UUID , Game> games = new HashMap<UUID , Game>();
	
	public GameManager(Main main) {
		this.main = main;
	}
	
	public UUID createGame(User owner , ArrayList<User> users, ArrayList<String> categories , int rounds) {
		UUID uuid = UUID.randomUUID();
		Game game = new Game(main , uuid , owner , users , categories , rounds);
		games.put(uuid , game);
		return uuid;
	}

}
