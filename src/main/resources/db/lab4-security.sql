-- =========================================================
-- LAB 4 - Script SQL de création des tables (base lab3_db)
-- Avec spring.jpa.hibernate.ddl-auto=update, Hibernate crée
-- ces tables automatiquement au démarrage de l'application.
-- Ce script est fourni comme livrable (étape 20).
-- =========================================================
CREATE DATABASE IF NOT EXISTS lab3_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lab3_db;

CREATE TABLE IF NOT EXISTS categories (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    price       DOUBLE,
    category_id BIGINT,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Utilisateurs (LAB 4, étape 4)
CREATE TABLE IF NOT EXISTS users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL
);

-- Commandes : user_id relie chaque commande à son propriétaire (LAB 4, étape 14)
CREATE TABLE IF NOT EXISTS orders (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date DATETIME(6),
    user_id    BIGINT,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity   INT,
    product_id BIGINT,
    order_id   BIGINT,
    CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id)
);
