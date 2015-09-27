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
	private Integer actualX;
	private Integer actualY;
	
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

	/**
	 * @return the actualX
	 */
	public Integer getActualX() {
		return actualX;
	}

	/**
	 * @param actualX the actualX to set
	 */
	public void setActualX(Integer actualX) {
		this.actualX = actualX;
	}

	/**
	 * @return the actualY
	 */
	public Integer getActualY() {
		return actualY;
	}

	/**
	 * @param actualY the actualY to set
	 */
	public void setActualY(Integer actualY) {
		this.actualY = actualY;
	}
}
