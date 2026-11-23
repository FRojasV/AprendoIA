package cl.triskeledu.juego.cliente;

import cl.triskeledu.juego.exception.ServicioNoDisponibleException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Comunicación entre microservicios: ms-juego le pregunta a ms-contenidos
 * si un código QR existe (y está publicado) y si un punto es hito turístico.
 */
@Component
public class ContenidosClient {

    private final RestClient restClient;

    public ContenidosClient(RestClient contenidosRestClient) {
        this.restClient = contenidosRestClient;
    }

    /** GET /api/capsulas/codigo/{codigo} en ms-contenidos. Vacío si no existe (404). */
    public Optional<CapsulaRemota> capsulaPorCodigo(String codigo) {
        return obtener("/api/capsulas/codigo/{codigo}", CapsulaRemota.class, codigo);
    }

    /** GET /api/capsulas/{id} en ms-contenidos. Vacío si no existe (404). */
    public Optional<CapsulaRemota> capsula(Long id) {
        return obtener("/api/capsulas/{id}", CapsulaRemota.class, id);
    }

    /** GET /api/puntos/{id} en ms-contenidos. Vacío si no existe (404). */
    public Optional<PuntoRemoto> punto(Long id) {
        return obtener("/api/puntos/{id}", PuntoRemoto.class, id);
    }

    private <T> Optional<T> obtener(String ruta, Class<T> tipo, Object variable) {
        try {
            return Optional.ofNullable(restClient.get().uri(ruta, variable).retrieve().body(tipo));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) return Optional.empty();
            throw e;
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("ms-contenidos no está disponible. ¿Está iniciado en el puerto 8082?");
        }
    }
}
