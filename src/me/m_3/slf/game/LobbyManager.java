package me.m_3.slf.game;

import java.util.HashMap;
import java.util.UUID;

import me.m_3.slf.Main;
import me.m_3.tiqoL.user.User;

import me.m_3.slf.game.Lobby;

public class LobbyManager {
	
	Main  main;
	
	HashMap<UUID , Lobby> lobbies = new HashMap<UUID , Lobby>();
	
	public LobbyManager(Main main) {
		this.main = main;
	}
	
	public Lobby createLobby (User user) {
		UUID uuid = UUID.randomUUID();
		Lobby lobby = new Lobby(main , uuid);
		lobby.setOwner(user);
		lobbies.put(uuid ,lobby);
		return lobby;
	}
	
	public Lobby getLobby(UUID uuid) {
		return lobbies.get(uuid);
	}
	
	public void unregisterLobby(UUID uuid) {
		lobbies.remove(uuid);
	}
	
	public Lobby findUser(User user) {
		for (Lobby lobby : lobbies.values()) {
			if (lobby.users.contains(user)) return lobby;
		}
		return null;
	}

	
}
