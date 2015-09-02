package br.com.engine.persistence.core;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import br.com.commons.transport.UserObject;

public class Test {
	public static void main(String[] args) {
		ConcurrentHashMap<String,Object> concurrentHashMap = new ConcurrentHashMap<>();
		System.out.println(new Date());
		for (int i = 0; i < 100; i++) {
			System.out.println(i);
			for (int j = 0; j < 600000; j++) {
				UserObject userObject = new UserObject();
				userObject.setName("nameDasNovinhas "+i);
				concurrentHashMap.put("i"+i+"j"+j, userObject);
			}			
		}
		System.out.println(new Date());
		System.out.println("\n\n\n\n\n\n\n\n\n");
		
		System.out.println(new Date());
		for (Iterator<Entry<String, Object>> iterator = concurrentHashMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			
		}
		System.out.println(new Date());
	}
}
