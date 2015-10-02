package br.com.engine.persistence.cache;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import br.com.commons.transport.CombatObject;

public class CombatCache {
	private static CombatCache instance;
	private static ConcurrentHashMap<UUID, CombatObject> repository;
	
	private CombatCache() {
		repository = new ConcurrentHashMap<>();
	}
	
	public static CombatCache getInstance() {
		if (instance == null) {
			instance = new CombatCache();
		}
		return instance;
	}
	
	public void add(UUID combatUUID, CombatObject lobby) {
		repository.put(combatUUID, lobby);
	}
	
	public CombatObject getLobby(String combatId) {
		return repository.get(combatId);
	}
}
