package com.web.empleadosws.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ErrorResponseDTO {

    private String codigo;
    private String mensaje;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

}
