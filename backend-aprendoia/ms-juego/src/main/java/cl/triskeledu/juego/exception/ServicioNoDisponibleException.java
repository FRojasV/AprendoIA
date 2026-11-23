package cl.triskeledu.juego.exception;

/** ms-contenidos no responde. Se traduce a HTTP 503. */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
