package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.model.DataStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        DataStore.inicializarDatos();
    }
}