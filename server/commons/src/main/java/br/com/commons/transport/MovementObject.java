package br.com.commons.transport;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "movement")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovementObject {
	
	private Deque<Integer> moves; // TODO change for PlaceObject's
	
	public MovementObject() {
		moves = new ArrayDeque<Integer>();
	}
	

	/**
	 * @return the moves
	 */
	public Deque<Integer> getMoves() {
		return moves;
	}

	/**
	 * @param moves the moves to set
	 */
	public void setMoves(Deque<Integer> moves) {
		this.moves = moves;
	}

}
