package com.bisma.foundation.fundamental_spring_boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component("appConfig")
public class AppConfiguration {
    private String name;
    private int version;
    private String greetingHello;

    private Feature feature = new Feature();


    public static class Feature{
        private int maxUsers;
        private boolean regisWithEmailAvail;

        public int getMaxUsers() {
            return maxUsers;
        }

        public void setMaxUsers(int maxUsers) {
            this.maxUsers = maxUsers;
        }

        public boolean getRegisWithEmailAvail() {
            return regisWithEmailAvail;
        }

        public void setRegisWithEmailAvail(boolean regisWithEmailAvail) {
            this.regisWithEmailAvail = regisWithEmailAvail;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getGreetingHello() {
        return greetingHello;
    }

    public void setGreetingHello(String greetingHello) {
        this.greetingHello = greetingHello;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }
}
