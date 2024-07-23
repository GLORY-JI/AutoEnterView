package com.ctrls.auto_enter_view.config;

import com.ctrls.auto_enter_view.component.TechStackDeserializer;
import com.ctrls.auto_enter_view.enums.TechStack;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

  @Bean
  public Module module() {

    SimpleModule simpleModule = new SimpleModule();

    // TechStack
    simpleModule.addDeserializer(TechStack.class, new TechStackDeserializer());

    // LocalDate
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));

    // LocalDateTime
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));

    return simpleModule;
  }
}