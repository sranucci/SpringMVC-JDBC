TRUNCATE TABLE tbl_user RESTART IDENTITY AND COMMIT NO CHECK;
TRUNCATE TABLE tbl_reset_password_token RESTART IDENTITY AND COMMIT NO CHECK;

-- Insert data into tbl_user
INSERT INTO tbl_user (user_id, email, password, name, last_name, is_admin)
VALUES (1, 'mail@maill.com', 'password', 'first name', 'last name', false);

INSERT INTO tbl_reset_password_token (id, user_id, token, expiration_date)
VALUES (1, 1, '7c734a3f-7e90-42f8-88e1-c031cc132961', '2023-05-17 12:34:56');