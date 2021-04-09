package com.square.blogging.config;

import com.square.blogging.service.UserService;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class InitConfig implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        userService.initAdminCheck();

        startH2Console();
    }

    private void startH2Console(){
        Runnable runServer = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("starting h2 server");
                    Server.startWebServer(DriverManager.getConnection("jdbc:h2:file:/home/setu/h2;DB_CLOSE_DELAY=-1", "sa", "password"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread threadServer = new Thread(runServer);
        threadServer.start();
    }
}
