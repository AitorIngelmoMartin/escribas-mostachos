-- User table
INSERT INTO users (email, username, password, books_read_count, role) VALUES ('a@b.com', 'username_1', '$2a$10$RjeE47YLF6vsbUDrJuL6.eRRTK5f7oS83824BflfBeJyXU6Jf39.y',0, 'ROLE_MODERATOR');
INSERT INTO users (email, username, password, first_name, last_name, books_read_count) VALUES ('e@f.com', 'username_2', '$2a$10$RjeE47YLF6vsbUDrJuL6.eRRTK5f7oS83824BflfBeJyXU6Jf39.y','NombreUsuario','ApellidoUsuario',0);

-- Books table
INSERT INTO books (isbn, title, author, cover_url, updated_by) VALUES
('9788408304753', 'El Quijote', 'Miguel de Cervantes', 'https://example.com/quijote.jpg', 'admin'),
('9788410163768', 'Cien Años de Soledad', 'Gabriel García Márquez', 'https://example.com/soledad.jpg', 'admin'),
('9788441532106', 'La Sombra del Viento', 'Carlos Ruiz Zafón', 'https://example.com/sombra.jpg', 'editor'),
('9798884444447', 'Rayuela', 'Julio Cortázar', 'https://example.com/rayuela.jpg', 'user1'),
('9788419822925', 'Pedro Páramo', 'Juan Rulfo', 'https://example.com/pedro.jpg', 'user2'),
('9788408305910', 'Don Juan Tenorio', 'José Zorrilla', 'https://example.com/donjuan.jpg', 'editor'),
('9788466660877', 'Ficciones', 'Jorge Luis Borges', 'https://example.com/ficciones.jpg', 'admin'),
('9788417347864', 'Los Detectives Salvajes', 'Roberto Bolaño', 'https://example.com/detectives.jpg', 'user3'),
('9788417347048', 'La Ciudad y los Perros', 'Mario Vargas Llosa', 'https://example.com/ciudad.jpg', 'admin'),
('9788417347468', 'Crónica de una Muerte Anunciada', 'Gabriel García Márquez', 'https://example.com/cronica.jpg', 'user2'),
('9788417347314', 'La Tía Julia y el Escribidor', 'Mario Vargas Llosa', 'https://example.com/julia.jpg', 'editor'),
('9788466659758', 'El Aleph', 'Jorge Luis Borges', 'https://example.com/aleph.jpg', 'admin'),
('9788416029747', 'Aura', 'Carlos Fuentes', 'https://example.com/aura.jpg', 'user1'),
('9788408302513', 'Los de Abajo', 'Mariano Azuela', 'https://example.com/abajo.jpg', 'user2'),
('9788411002189', 'La Casa de los Espíritus', 'Isabel Allende', 'https://example.com/espiritus.jpg', 'admin');