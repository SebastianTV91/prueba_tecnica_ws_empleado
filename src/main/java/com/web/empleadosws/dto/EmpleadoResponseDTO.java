package com.web.empleadosws.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpleadoResponseDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private String fechaNacimiento;
    private String fechaVinculacion;
    private String cargo;
    private Double salario;
    private TiempoDTO tiempoVinculacion;
    private TiempoDTO edadActual;

}
