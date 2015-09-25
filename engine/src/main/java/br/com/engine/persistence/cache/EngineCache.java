package br.com.engine.persistence.cache;

import java.util.concurrent.ConcurrentHashMap;

import br.com.commons.transport.MovementObject;
import br.com.commons.transport.UnitObject;

public class EngineCache {

	private static EngineCache instance;
	private static ConcurrentHashMap<UnitObject, MovementObject> repository;
	
	private EngineCache() {
		repository = new ConcurrentHashMap<UnitObject, MovementObject>();
	}
	
	public static EngineCache getInstance() {
		if (instance == null) {
			instance = new EngineCache();
		}
		return instance;
	}
	
	public void add(UnitObject army, MovementObject mov) {
		getRepository().put(army, mov);
	}
	
	public MovementObject findMovementByUnitId(Long id){
		MovementObject movementObject = this.getRepository().get(id);
		return movementObject;
	}

	/**
	 * @return the repository
	 */
	public ConcurrentHashMap<UnitObject, MovementObject> getRepository() {
		return repository;
	}
}
