package com.web.empleadosws.soap.endpoint;

import com.web.empleadosws.entities.Empleado;
import com.web.empleadosws.exception.EmpleadoExisteException;
import com.web.empleadosws.soap.model.RegistrarEmpleadoRequest;
import com.web.empleadosws.soap.model.RegistrarEmpleadoResponse;
import com.web.empleadosws.soap.service.EmpleadoSoapService;
import com.web.empleadosws.util.DateConverterUtil;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class EmpleadoEndpoint {

    private static final String NAMESPACE_URI = "http://parameta.com/empleado";

    private final EmpleadoSoapService empleadoSoapService;

    public EmpleadoEndpoint(EmpleadoSoapService empleadoSoapService) {
        this.empleadoSoapService = empleadoSoapService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registrarEmpleadoRequest")
    @ResponsePayload
    public RegistrarEmpleadoResponse registrarEmpleado(@RequestPayload RegistrarEmpleadoRequest request) {

        RegistrarEmpleadoResponse response = new RegistrarEmpleadoResponse();
        try {

            Empleado guardado = empleadoSoapService.guardar(
                    request.getNombres(),
                    request.getApellidos(),
                    request.getTipoDocumento(),
                    request.getNumeroDocumento(),
                    DateConverterUtil.localDate(request.getFechaNacimiento()),
                    DateConverterUtil.localDate(request.getFechaVinculacion()),
                    request.getCargo(),
                    request.getSalario()
            );

            response.setId(guardado.getId());
            response.setMensaje("Empleado registrado exitosamente en la base de datos");
            response.setExitoso(true);

        }catch(EmpleadoExisteException ex){
            response.setId(0L);
            response.setExitoso(false);
            response.setMensaje(ex.getMessage());
        } catch(Exception ex){
            response.setId(0L);
            response.setExitoso(false);
            response.setMensaje("Error interno al almacenar el empleado");
        }
        return response;
    }

}
