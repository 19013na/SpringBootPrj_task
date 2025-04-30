package com.rookies3.myspringbootlab.runner;

import com.rookies3.myspringbootlab.property.MyPropProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyPropRunner implements ApplicationRunner {

    @Value("${myprop.username}")
    String username;

    @Value("${myprop.port}")
    int port;


    @Autowired
    MyPropProperties myPropProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("1-4 username : {}", username);
        log.info("1-4 port : {}", port);

        log.debug("1-5 username : {}", myPropProperties.getUsername());
        log.debug("1-5 port : {}", myPropProperties.getPort());

    }
}
