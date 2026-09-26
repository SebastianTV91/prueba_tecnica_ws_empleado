package com.web.empleadosws.exception;

public class EmpleadoExisteException extends RuntimeException{

    public EmpleadoExisteException(String tipoDocumento, String numeroDocumento) {
        super("Ya existe un empleado con documento " + tipoDocumento + " " + numeroDocumento);
    }

}
