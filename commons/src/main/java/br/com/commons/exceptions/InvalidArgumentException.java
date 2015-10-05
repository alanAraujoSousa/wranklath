package br.com.commons.exceptions;

public class InvalidArgumentException extends RuntimeException implements
		ProjectException {

	private static final long serialVersionUID = -2471166903198746686L;
	
	public InvalidArgumentException() {
	}
	
	public InvalidArgumentException(String message) {
		super(message);
	}

	@Override
	public Integer getStatusCode() {
		return 403;
	}
}
