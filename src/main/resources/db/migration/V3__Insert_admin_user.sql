
-- Create Admin User
INSERT INTO users (first_name, last_name, email, password, role, email_confirmed, enabled)
VALUES (
           'Sistema',
           'Admin',
           'admin@sistema-nutricao.com',
           '$2a$10$pj6AOVxkXCw/9P8NPamWHO7FIf2gADbTFCY.P2qEz5mMzFD6D4oGq', -- password: senha123456
           'ADMIN',
           true,
           true
       );