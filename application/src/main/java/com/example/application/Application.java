package com.example.application;

import com.example.application.common.container.App;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class Application {

    public static void main(String[] args) {
        System.out.println("""
                \n_________ _______  ______   _______\s
                \\__    _/(  ___  )(  __  \\ (  ___  )
                   )  (  | (   ) || (  \\  )| (   ) |
                   |  |  | |   | || |   ) || (___) |
                   |  |  | |   | || |   | ||  ___  |
                   |  |  | |   | || |   ) || (   ) |
                |\\_)  )  | (___) || (__/  )| )   ( |
                (____/   (_______)(______/ |/     \\|
                                                   \s
                """);
        App.registerBeans(Set.of(
                "com.example.application.bpe.service",
                "com.example.application.gateway"
        ));
        App.createServer(8080);
        App.registerHandlers(Set.of(
                "com.example.application.gateway"
        ));
        App.startServer();
    }
}