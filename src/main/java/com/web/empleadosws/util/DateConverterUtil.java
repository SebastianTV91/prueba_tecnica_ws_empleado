package com.web.empleadosws.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class DateConverterUtil {

    private DateConverterUtil() {

    }

    public static XMLGregorianCalendar gregorianCalendar(LocalDate fecha) {

        try {

            GregorianCalendar calendario = GregorianCalendar.from(fecha.atStartOfDay(ZoneId.systemDefault()));
            XMLGregorianCalendar xmlFecha = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendario);

            xmlFecha.setTime(
                    javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED,
                    javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED,
                    javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED,
                    javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);

            return xmlFecha;

        } catch(DatatypeConfigurationException ex) {
            throw new IllegalStateException("Error al convertir la fecha para el mensaje SOAP", ex);
        }

    }

    public static LocalDate localDate(XMLGregorianCalendar fecha) {
        return LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay());
    }

}
