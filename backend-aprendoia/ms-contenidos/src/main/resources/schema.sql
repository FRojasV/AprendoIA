-- =====================================================================
-- ms-contenidos: estructura de la base de datos (se ejecuta al iniciar)
-- =====================================================================
CREATE TABLE IF NOT EXISTS puntos (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(60)   NOT NULL,
    descripcion         VARCHAR(200)  NOT NULL,
    info_turistica      VARCHAR(500),
    latitud             DOUBLE        NOT NULL,
    longitud            DOUBLE        NOT NULL,
    es_hito_turistico   BOOLEAN       NOT NULL DEFAULT FALSE,
    imagen_url          VARCHAR(300),
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS capsulas (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    punto_id        BIGINT        NOT NULL,
    titulo          VARCHAR(80)   NOT NULL,
    tema            VARCHAR(60)   NOT NULL,
    autor           VARCHAR(60)   NOT NULL,
    url             VARCHAR(300),
    duracion        VARCHAR(5),
    descripcion     VARCHAR(300),
    codigo_qr       VARCHAR(30)   NOT NULL,
    puntos          INT           NOT NULL DEFAULT 10,
    publicado       BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_capsulas_codigo UNIQUE (codigo_qr),
    CONSTRAINT fk_capsulas_punto FOREIGN KEY (punto_id) REFERENCES puntos (id) ON DELETE CASCADE
);
