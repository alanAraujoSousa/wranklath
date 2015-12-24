package br.com.commons.exceptions;


public class InvalidTokenException extends RuntimeException implements
		ProjectException {
	private static final long serialVersionUID = 9186654057885242047L;

	/**
	 * 
	 */
	public InvalidTokenException() {
	}

	/**
	 * @param message
	 */
	public InvalidTokenException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidTokenException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidTokenException(final String message, final Throwable cause) {
		super(message, cause);

	}

	@Override
	public Integer getStatusCode() {
		return 401;
	}
}
