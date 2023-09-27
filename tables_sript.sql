CREATE TABLE tb_books(
    id BIGSERIAL PRIMARY KEY,
    author VARCHAR(255),
    isbn VARCHAR(255),
    title VARCHAR(255)
);

CREATE TABLE tb_loan(
    id BIGSERIAL PRIMARY KEY,
    customer VARCHAR(255),
    email VARCHAR(255),
    loan_date DATE,
    returned BOOLEAN,
    book_id BIGINT,
    FOREIGN KEY (book_id) REFERENCES tb_books(id)
);