package com.cambistaonline.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ApiGatewayApplication: Punto de entrada (Main Entry Point) del Edge API Gateway.
 *
 * ¿CÓMO ARRANCA UNA APLICACIÓN EN JAVA?
 * Toda aplicación Java ejecutable requiere una función especial llamada:
 * 'public static void main(String[] args)'
 * Cuando ejecutas el programa, la Máquina Virtual de Java (JVM) busca exactamente este método.
 *
 * ¿QUÉ HACE LA ANOTACIÓN '@SpringBootApplication'?
 * En Java, las anotaciones que empiezan con '@' son metadatos que le dan superpoderes a la clase.
 * '@SpringBootApplication' es un atajo que combina 3 cosas esenciales de Spring Boot:
 * 1. @Configuration: Indica que esta clase define configuraciones de la aplicación.
 * 2. @EnableAutoConfiguration: Le dice a Spring que autoconfigure automáticamente librerías,
 *    rutas y servidores embebidos (en este caso Netty) según lo que encuentre en el pom.xml.
 * 3. @ComponentScan: Escanea este paquete y sus subpaquetes buscando componentes (@Component,
 *    @Service, @RestController) para registrarlos en memoria (IoC Container).
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        // Inicia el servidor reactivo Netty en el puerto 8080 y carga todos los filtros y rutas
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

