package cl.triskeledu.usuarios.exception;

/** Se traduce a HTTP 409 (por ejemplo, correo repetido o eliminar una cuenta del sistema). */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
