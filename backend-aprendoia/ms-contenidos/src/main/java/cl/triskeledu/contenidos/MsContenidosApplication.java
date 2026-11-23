package cl.triskeledu.contenidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de CONTENIDOS (puerto 8082).
 * Puntos turísticos georreferenciados de Santiago y cápsulas de IA con su código QR.
 */
@SpringBootApplication
public class MsContenidosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsContenidosApplication.class, args);
    }
}
