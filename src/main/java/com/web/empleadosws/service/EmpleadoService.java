package com.web.empleadosws.service;

import com.web.empleadosws.dto.EmpleadoRequestDTO;
import com.web.empleadosws.dto.EmpleadoResponseDTO;
import com.web.empleadosws.dto.TiempoDTO;
import com.web.empleadosws.exception.*;
import com.web.empleadosws.entities.Empleado;
import com.web.empleadosws.repository.EmpleadoRepository;
import com.web.empleadosws.soap.client.EmpleadoSoapClient;
import com.web.empleadosws.soap.model.RegistrarEmpleadoResponse;
import com.web.empleadosws.util.CalculadoraTiempoUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class EmpleadoService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int EDAD_MINIMA = 18;

    private final EmpleadoSoapClient empleadoSoapClient;
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoSoapClient empleadoSoapClient, EmpleadoRepository empleadoRepository) {
        this.empleadoSoapClient = empleadoSoapClient;
        this.empleadoRepository = empleadoRepository;
    }

    public EmpleadoResponseDTO procesarEmpleado(EmpleadoRequestDTO requestDTO) {

        validarCamposObligatorios(requestDTO);

        LocalDate fechaNacimiento = parsearFecha(requestDTO.getFechaNacimiento(), "fechaNacimiento");
        LocalDate fechaVinculacion = parsearFecha(requestDTO.getFechaVinculacion(), "fechaVinculacion");
        Double salario = parsearSalario(requestDTO.getSalario());

        validarFechasCoherentes(fechaNacimiento, fechaVinculacion);
        validarMayoriaEdad(fechaNacimiento);

        String tipoDocumento = requestDTO.getTipoDocumento().trim();
        String numeroDocumento = requestDTO.getNumeroDocumento().trim();
        validarNumeroDocumentoNoExiste(tipoDocumento, numeroDocumento);

        Empleado empleado = new Empleado();
        empleado.setNombres(requestDTO.getNombres().trim());
        empleado.setApellidos(requestDTO.getApellidos().trim());
        empleado.setTipoDocumento(requestDTO.getTipoDocumento().trim());
        empleado.setNumeroDocumento(requestDTO.getNumeroDocumento().trim());
        empleado.setFechaNacimiento(fechaNacimiento);
        empleado.setFechaVinculacion(fechaVinculacion);
        empleado.setCargo(requestDTO.getCargo().trim());
        empleado.setSalario(salario);

        RegistrarEmpleadoResponse soapResponse = empleadoSoapClient.registrarEmpleado(empleado);

        LocalDate hoy = LocalDate.now();
        TiempoDTO tiempoVinculacion = CalculadoraTiempoUtil.calcular(fechaVinculacion, hoy);
        TiempoDTO edadActual = CalculadoraTiempoUtil.calcular(fechaNacimiento, hoy);

        EmpleadoResponseDTO responseDTO = new EmpleadoResponseDTO();
        responseDTO.setId(soapResponse.getId());
        responseDTO.setNombres(empleado.getNombres());
        responseDTO.setApellidos(empleado.getApellidos());
        responseDTO.setTipoDocumento(empleado.getTipoDocumento());
        responseDTO.setNumeroDocumento(empleado.getNumeroDocumento());
        responseDTO.setFechaNacimiento(fechaNacimiento.format(FORMATO_FECHA));
        responseDTO.setFechaVinculacion(fechaVinculacion.format(FORMATO_FECHA));
        responseDTO.setCargo(empleado.getCargo());
        responseDTO.setSalario(empleado.getSalario());
        responseDTO.setTiempoVinculacion(tiempoVinculacion);
        responseDTO.setEdadActual(edadActual);

        return responseDTO;
    }

    private void validarCamposObligatorios(EmpleadoRequestDTO dto){

        if(esVacio(dto.getNombres())){
            throw new CampoVacioException("El campo 'nombres' es obligatorio y no puede estar vacío");
        }

        if(esVacio(dto.getApellidos())){
            throw new CampoVacioException("El campo 'apellidos' es obligatorio y no puede estar vacío");
        }

        if(esVacio(dto.getTipoDocumento())){
            throw new CampoVacioException("El campo 'tipoDocumento' es obligatorio y no puede estar vacío");
        }

        if(esVacio(dto.getNumeroDocumento())){
            throw new CampoVacioException("El campo 'numeroDocumento' es obligatorio y no puede estar vacío");
        }

        if(esVacio(dto.getFechaNacimiento())){
            throw new CampoVacioException("El campo 'fechaNacimiento' es obligatorio y no puede estar vacío");
        }

        if(esVacio(dto.getFechaVinculacion())){
            throw new CampoVacioException("El campo 'fechaVinculacion' es obligatorio y no puede estar vacío");
        }

        if(esVacio(dto.getCargo())){
            throw new CampoVacioException("El campo 'cargo' es obligatorio y no puede estar vacío");
        }

        if(esVacio(dto.getSalario())){
            throw new CampoVacioException("El campo 'salario' es obligatorio y no puede estar vacío");
        }

    }

    private boolean esVacio(String valor) {

        if (valor == null) {
            return true;
        }

        return valor.trim().isEmpty();

    }

    private LocalDate parsearFecha(String fecha, String nombreCampo) {

        try {
            return LocalDate.parse(fecha.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            throw new FormatoFechaInvalidoException(
                    "El campo '" + nombreCampo + "' tiene un formato de fecha inválido. Formato esperado: yyyy-MM-dd");
        }

    }

    private Double parsearSalario(String salario) {
        double valor;

        try {
            valor = Double.parseDouble(salario.trim());
        }catch (NumberFormatException ex) {
            throw new FormatoNumericoInvalidoException("El campo 'salario' debe ser un valor numérico válido");
        }

        if (valor <= 0) {
            throw new FormatoNumericoInvalidoException("El campo 'salario' debe ser un valor mayor a cero");
        }

        return valor;
    }

    private void validarFechasCoherentes(LocalDate fechaNacimiento,LocalDate fechaVinculacion) {

        LocalDate hoy = LocalDate.now();

        if (fechaNacimiento.isAfter(hoy)) {
            throw new FormatoFechaInvalidoException("La fecha de nacimiento no puede ser una fecha futura");
        }

        if (fechaVinculacion.isAfter(hoy)) {
            throw new FormatoFechaInvalidoException("La fecha de vinculación no puede ser una fecha futura");
        }

        if (fechaVinculacion.isBefore(fechaNacimiento)) {
            throw new FormatoFechaInvalidoException(
                    "La fecha de vinculación no puede ser anterior a la fecha de nacimiento");
        }

    }

    private void validarMayoriaEdad(LocalDate fechaNacimiento) {

        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();

        if (edad < EDAD_MINIMA) {
            throw new MenorEdadException("El empleado debe ser mayor de edad (18 años o más) para poder ser registrado");
        }

    }

    private void validarNumeroDocumentoNoExiste(String tipoDocumento, String numeroDocumento) {

        boolean existe = empleadoRepository.existsByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);

        if (existe) {
            throw new NumeroDocumentoDuplicadoException(
                    "Ya existe un empleado con documento " + tipoDocumento + " " + numeroDocumento);
        }

    }

}
