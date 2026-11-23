-- =====================================================================
-- ms-contenidos: 8 puntos de Santiago y 8 cápsulas de IA.
-- Los códigos QR son los mismos que trae la app (carpeta QR_AprendoIA).
-- =====================================================================
INSERT IGNORE INTO puntos (id, nombre, descripcion, info_turistica, latitud, longitud, es_hito_turistico) VALUES
(1, 'Plaza de Armas', 'Centro histórico y cívico de Santiago.', 'Fundada en 1541 por Pedro de Valdivia. La rodean la Catedral Metropolitana, el Correo Central y el Museo Histórico Nacional.', -33.4378, -70.6505, TRUE),
(2, 'Cerro Santa Lucía', 'Cerro urbano con terrazas, fuentes y miradores.', 'Lugar donde se fundó la ciudad. Desde el Castillo Hidalgo hay una de las mejores vistas del centro.', -33.4405, -70.6440, TRUE),
(3, 'Parque Forestal', 'Parque lineal junto al río Mapocho.', '', -33.4362, -70.6425, FALSE),
(4, 'Museo Nacional de Bellas Artes', 'Principal museo de artes visuales del país.', 'Edificio inaugurado en 1910 para el Centenario, inspirado en el Petit Palais de París.', -33.4343, -70.6433, TRUE),
(5, 'Barrio Lastarria', 'Barrio bohemio con cafés, librerías y centros culturales.', '', -33.4386, -70.6392, FALSE),
(6, 'Palacio de La Moneda', 'Sede del Presidente de la República.', 'Construido entre 1784 y 1805 como casa de moneda. Bajo la plaza se encuentra el Centro Cultural La Moneda.', -33.4429, -70.6540, TRUE),
(7, 'Cerro San Cristóbal', 'Parque Metropolitano con vista panorámica de la ciudad.', 'En su cumbre está el Santuario de la Inmaculada Concepción. Se puede subir en funicular o teleférico.', -33.4253, -70.6330, TRUE),
(8, 'Biblioteca Nacional', 'Una de las bibliotecas más antiguas de Sudamérica.', '', -33.4421, -70.6456, FALSE);

INSERT IGNORE INTO capsulas (id, punto_id, titulo, tema, autor, url, duracion, descripcion, codigo_qr, puntos, publicado) VALUES
(1, 1, 'Introducción a la Inteligencia Artificial', 'Fundamentos de IA', 'Triskeledu', '', '2:15', 'Qué es la IA, cómo funciona y ejemplos de su uso en la vida diaria.', 'QR-PLAZA-ARMAS', 10, TRUE),
(2, 2, 'IA en la Vida Diaria', 'Aplicaciones', 'Triskeledu', '', '1:50', 'Asistentes de voz, recomendaciones y mapas: la IA que usas sin notarlo.', 'QR-SANTA-LUCIA', 10, TRUE),
(3, 3, 'Robótica e IA', 'Robótica', 'Triskeledu', '', '2:30', 'Cómo los robots perciben el entorno y toman decisiones.', 'QR-PARQUE-FORESTAL', 10, TRUE),
(4, 4, 'IA y el Medio Ambiente', 'Sostenibilidad', 'Triskeledu', '', '2:05', 'Modelos que predicen el clima y ayudan a cuidar los recursos naturales.', 'QR-BELLAS-ARTES', 10, TRUE),
(5, 5, 'Futuro de la IA', 'Tendencias', 'Triskeledu', '', '2:40', 'Hacia dónde avanza la IA y qué habilidades serán clave.', 'QR-LASTARRIA', 10, TRUE),
(6, 6, 'Machine Learning en 2 minutos', 'Aprendizaje automático', 'Triskeledu', '', '2:00', 'Cómo una máquina aprende a partir de ejemplos.', 'QR-LA-MONEDA', 10, TRUE),
(7, 7, 'Redes Neuronales', 'Deep Learning', 'Triskeledu', '', '2:20', 'Neuronas artificiales, capas y entrenamiento explicados con ejemplos simples.', 'QR-SAN-CRISTOBAL', 10, TRUE),
(8, 8, 'IA Generativa', 'IA Generativa', 'Triskeledu', '', '2:10', 'Modelos que crean texto, imágenes y música. Usos responsables.', 'QR-BIBLIOTECA', 10, TRUE);
