package com.supermarket.config;

import com.supermarket.util.DatabaseConnection;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    @PostConstruct
    public void init() {
        DatabaseConnection.configure(url, user, password);
    }
}
