package com.example.Simulador_de_Encomendas_em_Drone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimuladorDeEncomendasEmDroneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimuladorDeEncomendasEmDroneApplication.class, args);
	}

}
