-- =====================================================================
-- ms-usuarios: datos iniciales. INSERT IGNORE no duplica si ya existen.
-- Contraseñas en SHA-256:  "123"  -> a665a459...  |  "1234" -> 03ac6742...
-- =====================================================================

-- Cuentas en duro exigidas por el caso
INSERT IGNORE INTO usuarios (id, nombre, alias, correo, telefono, password_hash, direccion, avatar, rol, notificaciones_activas)
VALUES (1, 'Administrador', 'Admin', 'admin@triskeledu.cl', '', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '', 'avatar_admin', 'ADMIN', TRUE);

INSERT IGNORE INTO usuarios (id, nombre, alias, correo, telefono, password_hash, direccion, avatar, rol, notificaciones_activas)
VALUES (2, 'Contenido', 'GenCont', 'contenido@triskeledu.cl', '', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '', 'avatar_creador', 'CREADOR', TRUE);

-- Jugadores de demostración (contraseña 1234)
INSERT IGNORE INTO usuarios (id, nombre, alias, correo, telefono, password_hash, direccion, avatar, rol, notificaciones_activas)
VALUES (3, 'Ana González', 'Ana', 'ana@email.com', '+56 9 1234 5678', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'Providencia, Santiago', 'avatar_1', 'CLIENTE', TRUE);

INSERT IGNORE INTO usuarios (id, nombre, alias, correo, telefono, password_hash, direccion, avatar, rol, notificaciones_activas)
VALUES (4, 'Carlos Muñoz', 'Carlos', 'carlos@email.com', '+56 9 8765 4321', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'Ñuñoa, Santiago', 'avatar_3', 'CLIENTE', TRUE);
