package com.web.empleadosws.soap.service;

import com.web.empleadosws.entities.Empleado;
import com.web.empleadosws.exception.EmpleadoExisteException;
import com.web.empleadosws.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EmpleadoSoapService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoSoapService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Empleado guardar(String nombres, String apellidos, String tipoDocumento, String numeroDocumento,
                            LocalDate fechaNacimiento, LocalDate fechaVinculacion, String cargo, Double salario){

        if (empleadoRepository.existsByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)){
            throw new EmpleadoExisteException(tipoDocumento, numeroDocumento);
        }

        Empleado empleado = new Empleado();
        empleado.setNombres(nombres);
        empleado.setApellidos(apellidos);
        empleado.setTipoDocumento(tipoDocumento);
        empleado.setNumeroDocumento(numeroDocumento);
        empleado.setFechaNacimiento(fechaNacimiento);
        empleado.setFechaVinculacion(fechaVinculacion);
        empleado.setCargo(cargo);
        empleado.setSalario(salario);

        return empleadoRepository.save(empleado);

    }


}
