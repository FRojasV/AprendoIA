package cl.triskeledu.usuarios.exception;

/** Se traduce a HTTP 401. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
