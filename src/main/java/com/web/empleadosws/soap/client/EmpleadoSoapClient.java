package com.web.empleadosws.soap.client;

import com.web.empleadosws.exception.ServicioSoapException;
import com.web.empleadosws.entities.Empleado;
import com.web.empleadosws.soap.model.RegistrarEmpleadoRequest;
import com.web.empleadosws.soap.model.RegistrarEmpleadoResponse;
import com.web.empleadosws.util.DateConverterUtil;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class EmpleadoSoapClient extends WebServiceGatewaySupport {

    public RegistrarEmpleadoResponse registrarEmpleado(Empleado empleado) {
        RegistrarEmpleadoRequest request = new RegistrarEmpleadoRequest();
        request.setNombres(empleado.getNombres());
        request.setApellidos(empleado.getApellidos());
        request.setTipoDocumento(empleado.getTipoDocumento());
        request.setNumeroDocumento(empleado.getNumeroDocumento());
        request.setFechaNacimiento(DateConverterUtil.gregorianCalendar(empleado.getFechaNacimiento()));
        request.setFechaVinculacion(DateConverterUtil.gregorianCalendar(empleado.getFechaVinculacion()));
        request.setCargo(empleado.getCargo());
        request.setSalario(empleado.getSalario());

        try {
            Object respuesta = getWebServiceTemplate().marshalSendAndReceive(request);
            return (RegistrarEmpleadoResponse) respuesta;
        } catch (Exception ex) {
            throw new ServicioSoapException(
                    "No fue posible registrar el empleado a través del servicio SOAP: " + ex.getMessage(), ex);
        }
    }

}
