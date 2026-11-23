-- =====================================================================
-- ms-usuarios: estructura de la base de datos (se ejecuta al iniciar)
-- =====================================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    nombre                  VARCHAR(80)  NOT NULL,
    alias                   VARCHAR(40)  NOT NULL,
    correo                  VARCHAR(120) NOT NULL,
    telefono                VARCHAR(20),
    password_hash           VARCHAR(64)  NOT NULL,
    direccion               VARCHAR(150),
    avatar                  VARCHAR(30)  NOT NULL DEFAULT 'avatar_1',
    rol                     VARCHAR(10)  NOT NULL,
    notificaciones_activas  BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_registro          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_correo UNIQUE (correo)
);
