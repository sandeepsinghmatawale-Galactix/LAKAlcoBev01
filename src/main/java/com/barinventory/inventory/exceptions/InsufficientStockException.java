package com.barinventory.inventory.exceptions;


public class InsufficientStockException extends RuntimeException {
 public InsufficientStockException(String message) { super(message); }
}