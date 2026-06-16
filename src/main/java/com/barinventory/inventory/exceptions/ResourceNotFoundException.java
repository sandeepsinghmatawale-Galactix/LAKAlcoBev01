package com.barinventory.inventory.exceptions;

//inventory/exceptions/ResourceNotFoundException.java

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}