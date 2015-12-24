package br.com.commons.exceptions;



public class InvalidLoginException extends RuntimeException implements ProjectException{
	private static final long serialVersionUID = 9186654057885242047L;

	/**
	 * 
	 */
	public InvalidLoginException() {
	}

	/**
	 * @param message
	 */
	public InvalidLoginException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidLoginException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidLoginException(final String message, final Throwable cause) {
		super(message, cause);
	}

	@Override
	public Integer getStatusCode() {
		return 405;
	}

}
