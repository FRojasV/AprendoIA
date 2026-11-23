package cl.triskeledu.juego;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de JUEGO (puerto 8083).
 * Registra escaneos de QR (+10), selfies en hitos (+100), historial, estadísticas y ranking.
 * Consulta a ms-contenidos (8082) para validar cada código QR.
 */
@SpringBootApplication
public class MsJuegoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsJuegoApplication.class, args);
    }
}
