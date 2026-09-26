package com.web.empleadosws.exception;

import com.web.empleadosws.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CampoVacioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarCampoVacio(CampoVacioException ex){
        ErrorResponseDTO error = new ErrorResponseDTO("CAMPO_VACIO", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FormatoFechaInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarFormatoFecha(FormatoFechaInvalidoException ex){
        ErrorResponseDTO error = new ErrorResponseDTO("FORMATO_FECHA_INVALIDO", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FormatoNumericoInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarFormatoNumerico(FormatoNumericoInvalidoException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("FORMATO_NUMERICO_INVALIDO", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MenorEdadException.class)
    public ResponseEntity<ErrorResponseDTO> manejarMenorEdad(MenorEdadException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("EMPLEADO_MENOR_DE_EDAD", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServicioSoapException.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorSoap(ServicioSoapException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("ERROR_SERVICIO_SOAP", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("ERROR_INTERNO", "Ocurrió un error inesperado: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
