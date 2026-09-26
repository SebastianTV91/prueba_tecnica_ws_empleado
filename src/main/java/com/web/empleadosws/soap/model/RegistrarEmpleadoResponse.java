package com.web.empleadosws.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@XmlRootElement(name = "registrarEmpleadoResponse", namespace = "http://parameta.com/empleado")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistrarEmpleadoResponse {

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private Long id;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private String mensaje;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private boolean exitoso;
}
