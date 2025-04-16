-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

-- Таблица товаров
CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT NOT NULL,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Таблица бронирований
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT NOT NULL,
    booker_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (booker_id) REFERENCES users(id),
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items(id)
);

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT NOT NULL,
    text VARCHAR(255) NOT NULL,
    author_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    comment_date TIMESTAMP NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items(id)
);
