package com.soa.micro.service.inventory.exeception;

public class ModifidException extends RuntimeException {

	private static final long serialVersionUID = 928421818937263886L;

	public ModifidException() {
		super();
	}

	public ModifidException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModifidException(String message) {
		super(message);
	}

}
