package me.m_3.slf.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import me.m_3.slf.Main;
import me.m_3.tiqoL.user.User;

public class GameManager {
	
	Main main;
	
	HashMap<UUID , Game> games = new HashMap<UUID , Game>();
	
	public GameManager(Main main) {
		this.main = main;
	}
	
	public UUID createGame(User owner , ArrayList<User> users, ArrayList<String> categories , Set<String> chars , int rounds , int seconds , boolean voting , boolean stop) {
		UUID uuid = UUID.randomUUID();
		Game game = new Game(main , uuid , owner , users , categories , rounds , chars , seconds , voting , stop);
		games.put(uuid , game);
		return uuid;
	}
	
	public Game findUser(User user) {
		for (Game game : games.values()) {
			if (game.users.contains(user)) return game;
		}
		return null;
	}
	
	public void unregister (Game game) {
		this.games.remove(game.uuid);
	}

}
