package com.i10n.fleet.exceptions;

public class OperationNotSupportedException extends Exception{
    
	private static final long serialVersionUID = 1L;

	public OperationNotSupportedException(String message){
        super(message);
    }
    
}
