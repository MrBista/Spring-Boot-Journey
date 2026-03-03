package com.bisma.foundation.aop_learn.service;

public class EmailNotification implements NotificationService{
    private final int port;
    private final String host;
    private final String username;
    private final String password;

    public EmailNotification(int port, String host, String username, String password) {
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    @Override
    public void send(String to, String message) {

    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
