package br.com.commons.exceptions;


public class InvalidPermissionException extends RuntimeException implements
		ProjectException {

	private static final long serialVersionUID = 6591320628067229986L;

	public InvalidPermissionException() {
	}

	public InvalidPermissionException(final String string) {
		super(string);
	}

	@Override
	public Integer getStatusCode() {
		return 401;
	}
}
