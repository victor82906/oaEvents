package com.vmr.oaevents.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // La URL a la que se conectará el frontend (ej. ws://localhost:8080/ws-chat)
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefijo para enviar mensajes desde el servidor al cliente
        registry.enableSimpleBroker("/user");
        // Prefijo para enviar mensajes desde el cliente al servidor (opcional si usas REST para enviar)
        registry.setApplicationDestinationPrefixes("/app");
    }

}
