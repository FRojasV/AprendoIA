package cl.triskeledu.juego.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Cliente HTTP para comunicarse con el microservicio ms-contenidos. */
@Configuration
public class ClientesConfig {

    @Bean
    public RestClient contenidosRestClient(@Value("${servicios.contenidos.url}") String urlBase) {
        return RestClient.builder().baseUrl(urlBase).build();
    }
}
