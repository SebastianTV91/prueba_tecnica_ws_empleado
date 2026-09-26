package com.web.empleadosws.controller;

import com.web.empleadosws.dto.EmpleadoRequestDTO;
import com.web.empleadosws.dto.EmpleadoResponseDTO;
import com.web.empleadosws.service.EmpleadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public ResponseEntity<EmpleadoResponseDTO> registrarEmpleado(
            @RequestParam(name = "nombres", required = false) String nombres,
            @RequestParam(name = "apellidos", required = false) String apellidos,
            @RequestParam(name = "tipoDocumento", required = false) String tipoDocumento,
            @RequestParam(name = "numeroDocumento", required = false) String numeroDocumento,
            @RequestParam(name = "fechaNacimiento", required = false) String fechaNacimiento,
            @RequestParam(name = "fechaVinculacion", required = false) String fechaVinculacion,
            @RequestParam(name = "cargo", required = false) String cargo,
            @RequestParam(name = "salario", required = false) String salario) {

        EmpleadoRequestDTO requestDTO = new EmpleadoRequestDTO(
                nombres, apellidos, tipoDocumento, numeroDocumento,
                fechaNacimiento, fechaVinculacion, cargo, salario);

        EmpleadoResponseDTO responseDTO = empleadoService.procesarEmpleado(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

}
