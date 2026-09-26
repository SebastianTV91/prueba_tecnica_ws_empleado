package com.web.empleadosws.config;

import com.web.empleadosws.soap.client.EmpleadoSoapClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class WebServiceClientConfig {

    @Bean
    public EmpleadoSoapClient empleadoSoapClient(@Value("${soap.service.uri}") String soapServiceUri) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.web.empleadosws.soap.model");

        EmpleadoSoapClient client = new EmpleadoSoapClient();
        client.setDefaultUri(soapServiceUri);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}
