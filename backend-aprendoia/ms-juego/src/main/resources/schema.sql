-- =====================================================================
-- ms-juego: estructura de la base de datos (se ejecuta al iniciar)
-- usuario_id, capsula_id y punto_id apuntan a OTROS microservicios,
-- por eso no son claves foráneas (cada microservicio tiene su propia base).
-- =====================================================================
CREATE TABLE IF NOT EXISTS descubrimientos (
    id           BIGINT     NOT NULL AUTO_INCREMENT,
    usuario_id   BIGINT     NOT NULL,
    capsula_id   BIGINT     NOT NULL,
    punto_id     BIGINT     NOT NULL,
    puntos       INT        NOT NULL,
    visto        BOOLEAN    NOT NULL DEFAULT FALSE,
    fecha        TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_visto  TIMESTAMP  NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_desc_usuario_capsula UNIQUE (usuario_id, capsula_id)
);

CREATE TABLE IF NOT EXISTS selfies (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT        NOT NULL,
    punto_id    BIGINT        NOT NULL,
    ruta_foto   VARCHAR(300)  NOT NULL,
    puntos      INT           NOT NULL DEFAULT 100,
    fecha       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_selfie_usuario_punto UNIQUE (usuario_id, punto_id)
);

CREATE TABLE IF NOT EXISTS historial (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT        NOT NULL,
    tipo        VARCHAR(10)   NOT NULL,
    titulo      VARCHAR(80)   NOT NULL,
    detalle     VARCHAR(200),
    puntos      INT           NOT NULL DEFAULT 0,
    fecha       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
