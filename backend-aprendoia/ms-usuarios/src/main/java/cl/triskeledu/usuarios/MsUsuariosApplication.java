package cl.triskeledu.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de USUARIOS (puerto 8081).
 * Registro, inicio de sesión y CRUD de jugadores, Administrador y Creador de Contenido.
 */
@SpringBootApplication
public class MsUsuariosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsUsuariosApplication.class, args);
    }
}
