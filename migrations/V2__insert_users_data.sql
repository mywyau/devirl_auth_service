-- Insert sample users into the users table
-- Passwords should be securely hashed in practice (these are plain text here for demonstration purposes)

-- Insert regular users
INSERT INTO users (name, email, password_hash, role)
VALUES
('John Doe', 'john@example.com', 'hashedpassword123', 'user'),
('Jane Smith', 'jane@example.com', 'hashedpassword456', 'user'),
('Bob Smith', 'bob@example.com', 'hashedpassword456', 'user'),
('Mikey Smith', 'mikey@example.com', 'hashedpassword456', 'user'),
('Kurtis Smith', 'kurtis@example.com', 'hashedpassword456', 'user');

-- Insert business users
INSERT INTO users (name, email, password_hash, role)
VALUES
('Bob Business', 'bob@business.com', 'hashedpassword789', 'business'),
('Alice Business', 'alice@business.com', 'hashedpassword101', 'business');

-- Insert admin user
INSERT INTO users (name, email, password_hash, role)
VALUES
('Admin User', 'admin@example.com', 'adminhashedpassword', 'admin');
