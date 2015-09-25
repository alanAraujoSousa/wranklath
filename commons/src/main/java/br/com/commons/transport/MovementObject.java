package br.com.commons.transport;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.commons.enums.UnitIntentEnum;


@XmlRootElement(name = "movement")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovementObject {
	
	private Date timeToNextMove;
	private UnitIntentEnum unitIntent;
	private Long targetId;
	private Deque<Integer> moves;
	private Integer actualX;
	private Integer actualY;
	
	public MovementObject() {
		moves = new ArrayDeque<Integer>();
	}
	
	/**
	 * @return the timeToNextMove
	 */
	public Date getTimeToNextMove() {
		return timeToNextMove;
	}

	/**
	 * @param timeToNextMove the timeToNextMove to set
	 */
	public void setTimeToNextMove(Date timeToNextMove) {
		this.timeToNextMove = timeToNextMove;
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
	 * @return the unitIntent
	 */
	public UnitIntentEnum getUnitIntent() {
		return unitIntent;
	}

	/**
	 * @param unitIntent the unitIntent to set
	 */
	public void setUnitIntent(UnitIntentEnum unitIntent) {
		this.unitIntent = unitIntent;
	}

	/**
	 * @return the targetId
	 */
	public Long getTargetId() {
		return targetId;
	}

	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
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
