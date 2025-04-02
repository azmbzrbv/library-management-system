insert into Book(id, title, author, isbn, available)
values (1, 'Don Quixote', 'Miguel de Cervantes', '978-0060934347', true);

insert into Book(id, title, author, isbn, available)
values (2, 'Pride and Prejudice', 'Jane Austen', '978-1503290563', true);

insert into Book(id, title, author, isbn, available)
values (3, 'The Hobbit', 'J.R.R. Tolkien', '978-0547928227', true);




-- Admin user password is adminPass123
INSERT INTO Users(id, name, email, password, role, approved) 
VALUES (1001, 'Admin User', 'admin@example.com', '$2y$10$ER8Ez7Uttxi4HOmH92hH.uImQdE9NesnbMAT7coJIkfN2j4hlwKIq', 'ADMIN', true);  

-- Regular user 1 password is password123
INSERT INTO Users(id, name, email, password, role, approved) 
VALUES (1002, 'John Doe', 'johndoe@example.com', '$2y$10$BTbiQ43qPjaZR/Mnid9keus76g7cH4rEmJll1aKv3JPTDmpkpuAvG', 'USER', true);

-- Regular user 2 password is password123
INSERT INTO Users(id, name, email, password, role, approved) 
VALUES (1003, 'Jane Smith', 'janesmith@example.com', '$2y$10$GGGPnSE3rzazbha3crtvjeW1klkBMqoSLhA7Z1W8msaZn3ce9F3qO', 'USER', true);


-- Regular user 3  (not approved)  //password is password123
INSERT INTO Users(id, name, email, password, role, approved) 
VALUES (1004, 'Azim', 'bzrbvazm@gmail.com', '$2y$10$QLBkBgdQdi7T.YkybkukUeIaMmf8x4bf0cBOusjs8jDFK6Ex96SH6', 'USER', false);




-- Loaned "Pride and Prejudice" to John Doe
INSERT INTO Loan(id, book_id, user_id, loan_date, return_date, returned)
VALUES (1, 2, 1002, '2025-03-01', '2025-03-15', false);

-- Loaned "The Hobbit" to John Doe, returned
INSERT INTO Loan(id, book_id, user_id, loan_date, return_date, returned)
VALUES (2, 3, 1002, '2025-02-15', '2025-02-28', true);

-- Loaned "Don Quoxote" to Jane Smith (not returned yet)
INSERT INTO Loan(id, book_id, user_id, loan_date, return_date, returned)
VALUES (3, 1, 1003, '2025-03-10', '2025-03-25', false);




