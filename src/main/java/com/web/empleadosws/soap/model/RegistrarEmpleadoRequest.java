package com.web.empleadosws.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import lombok.Getter;
import lombok.Setter;

import javax.xml.datatype.XMLGregorianCalendar;

@Getter
@Setter
@XmlRootElement(name = "registrarEmpleadoRequest", namespace = "http://parameta.com/empleado")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistrarEmpleadoRequest {

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private String nombres;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private String apellidos;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private String tipoDocumento;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private String numeroDocumento;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar fechaNacimiento;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar fechaVinculacion;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private String cargo;

    @XmlElement(namespace = "http://parameta.com/empleado", required = true)
    private Double salario;

}
