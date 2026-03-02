package com.bisma.foundation.spring_core_ioc_di.service;

public class EmailNotificationService implements NotificationService{
    private final String host;
    private final int port;
    private final String username;

    public EmailNotificationService(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    @Override
    public void send(String to, String message) {
        System.out.println("[EmailNotificationService] mengirim dari " + username + "@" + host + ":" + port);
        System.out.println("[EmailNotificationService] to " + to);
        System.out.println("[EmailNotificationService] Message: " + message);
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}
