CREATE TABLE IF NOT EXISTS tb_book (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
    );

CREATE INDEX idx_book_name ON tb_book(name);
CREATE INDEX idx_book_author ON tb_book(author);