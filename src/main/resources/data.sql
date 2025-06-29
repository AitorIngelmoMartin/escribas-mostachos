-- User table
INSERT INTO users (email, username, password) VALUES ('a@b.com', 'username_1', '$2a$10$RjeE47YLF6vsbUDrJuL6.eRRTK5f7oS83824BflfBeJyXU6Jf39.y');
INSERT INTO users (email, username, password, first_name, last_name) VALUES ('e@f.com', 'username_2', '$2a$10$RjeE47YLF6vsbUDrJuL6.eRRTK5f7oS83824BflfBeJyXU6Jf39.y','NombreUsuario','ApellidoUsuario');

-- Books table
INSERT INTO books (isbn, title, author, cover_url, updated_by) VALUES
('9780000000001', 'El Quijote', 'Miguel de Cervantes', 'https://example.com/quijote.jpg', 'admin'),
('9780000000002', 'Cien Años de Soledad', 'Gabriel García Márquez', 'https://example.com/soledad.jpg', 'admin'),
('9780000000003', 'La Sombra del Viento', 'Carlos Ruiz Zafón', 'https://example.com/sombra.jpg', 'editor'),
('9780000000004', 'Rayuela', 'Julio Cortázar', 'https://example.com/rayuela.jpg', 'user1'),
('9780000000005', 'Pedro Páramo', 'Juan Rulfo', 'https://example.com/pedro.jpg', 'user2'),
('9780000000006', 'Don Juan Tenorio', 'José Zorrilla', 'https://example.com/donjuan.jpg', 'editor'),
('9780000000007', 'Ficciones', 'Jorge Luis Borges', 'https://example.com/ficciones.jpg', 'admin'),
('9780000000008', 'Los Detectives Salvajes', 'Roberto Bolaño', 'https://example.com/detectives.jpg', 'user3'),
('9780000000009', 'La Ciudad y los Perros', 'Mario Vargas Llosa', 'https://example.com/ciudad.jpg', 'admin'),
('9780000000010', 'Crónica de una Muerte Anunciada', 'Gabriel García Márquez', 'https://example.com/cronica.jpg', 'user2'),
('9780000000011', 'La Tía Julia y el Escribidor', 'Mario Vargas Llosa', 'https://example.com/julia.jpg', 'editor'),
('9780000000012', 'El Aleph', 'Jorge Luis Borges', 'https://example.com/aleph.jpg', 'admin'),
('9780000000013', 'Aura', 'Carlos Fuentes', 'https://example.com/aura.jpg', 'user1'),
('9780000000014', 'Los de Abajo', 'Mariano Azuela', 'https://example.com/abajo.jpg', 'user2'),
('9780000000015', 'La Casa de los Espíritus', 'Isabel Allende', 'https://example.com/espiritus.jpg', 'admin');