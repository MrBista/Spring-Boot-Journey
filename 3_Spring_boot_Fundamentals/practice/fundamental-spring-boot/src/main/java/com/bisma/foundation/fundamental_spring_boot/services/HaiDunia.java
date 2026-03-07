package com.bisma.foundation.fundamental_spring_boot.services;

import com.bisma.foundation.fundamental_spring_boot.config.AppConfiguration;
import org.springframework.stereotype.Service;

@Service("haiDunia")
public class HaiDunia implements HelloWorld{

    private final AppConfiguration configuration;

    public HaiDunia(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String helloWorld(String name) {
        return "hai Dunia " + name + " how's life ?" + " data dari aplikasi " + configuration.getName() + " Dengan Version " + configuration.getVersion();
    }
}
