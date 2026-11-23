-- =====================================================================
-- ms-juego: progreso de demostración.
-- Ana (id 3): 2 QR, 1 cápsula vista y 1 selfie = 120 puntos.
-- Carlos (id 4): 1 QR = 10 puntos.
-- =====================================================================
INSERT IGNORE INTO descubrimientos (id, usuario_id, capsula_id, punto_id, puntos, visto, fecha, fecha_visto) VALUES
(1, 3, 2, 2, 10, TRUE,  '2026-11-20 10:15:00', '2026-11-20 10:20:00'),
(2, 3, 5, 5, 10, FALSE, '2026-11-21 16:40:00', NULL),
(3, 4, 6, 6, 10, FALSE, '2026-11-22 12:05:00', NULL);

INSERT IGNORE INTO selfies (id, usuario_id, punto_id, ruta_foto, puntos, fecha) VALUES
(1, 3, 2, 'demo://selfie-santa-lucia', 100, '2026-11-20 10:30:00');

INSERT IGNORE INTO historial (id, usuario_id, tipo, titulo, detalle, puntos, fecha) VALUES
(1, 3, 'QR',     'Escaneaste un QR',         'IA en la Vida Diaria · Cerro Santa Lucía', 10,  '2026-11-20 10:15:00'),
(2, 3, 'VIDEO',  'Viste una cápsula de IA',  'IA en la Vida Diaria',                     0,   '2026-11-20 10:20:00'),
(3, 3, 'SELFIE', 'Selfie en hito turístico', 'Cerro Santa Lucía',                        100, '2026-11-20 10:30:00'),
(4, 3, 'QR',     'Escaneaste un QR',         'Futuro de la IA · Barrio Lastarria',       10,  '2026-11-21 16:40:00'),
(5, 4, 'QR',     'Escaneaste un QR',         'Machine Learning en 2 minutos · Palacio de La Moneda', 10, '2026-11-22 12:05:00');
