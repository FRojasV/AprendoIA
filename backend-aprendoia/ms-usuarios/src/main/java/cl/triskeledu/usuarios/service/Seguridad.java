package cl.triskeledu.usuarios.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Hash SHA-256 de contraseñas (mismo algoritmo que usa la app en util/Seguridad.kt). */
public final class Seguridad {

    private Seguridad() {
    }

    public static String hash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public static boolean coincide(String textoPlano, String hashGuardado) {
        return hash(textoPlano).equalsIgnoreCase(hashGuardado);
    }
}
