package br.com.engine.business.scheduler;

import br.com.commons.transport.UnitObject;

public class Test {
	public static void main(String[] args) {
		UnitObject u = new UnitObject();
		u.setQuantity(3);
		
		Integer quantity = u.getQuantity();
		quantity = 4;
		
		System.out.println(u.getQuantity());
	}
}
