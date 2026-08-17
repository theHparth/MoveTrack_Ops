package com.movetrackops.soapadapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class XsdSchemaBean {
    @Bean
    public SimpleXsdSchema moveRequestSchema() {
        return new SimpleXsdSchema(new ClassPathResource("moveRequest.xsd"));
    }
}