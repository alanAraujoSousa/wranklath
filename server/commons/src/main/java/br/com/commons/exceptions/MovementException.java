package br.com.commons.exceptions;

public class MovementException extends RuntimeException implements
		ProjectException {

	private static final long serialVersionUID = 7834397021559472113L;
	
	public MovementException() {
	}

	public MovementException(String message) {
		super(message);
	}

	@Override
	public Integer getStatusCode() {
		return 403;
	}
}
