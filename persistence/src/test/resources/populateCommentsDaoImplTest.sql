TRUNCATE TABLE tbl_user RESTART IDENTITY AND COMMIT NO CHECK;
TRUNCATE TABLE tbl_difficulty RESTART IDENTITY AND COMMIT NO CHECK;
TRUNCATE TABLE tbl_recipe RESTART IDENTITY AND COMMIT NO CHECK;
TRUNCATE TABLE tbl_comment RESTART IDENTITY AND COMMIT NO CHECK;

-- Insert data into tbl_user
INSERT INTO tbl_user (user_id, email, password, name, last_name, is_admin)
VALUES (1, 'mail@maill.com', 'password', 'first name', 'last name', false),
       (2, 'mail2@mail.com', 'password', 'first name', 'last name', true);

INSERT INTO tbl_difficulty (difficulty_name)
VALUES ('easy'),
       ('intermediate'),
       ('hard');

INSERT INTO tbl_recipe (recipe_id, title, description, user_id, is_private, total_minutes, difficulty_id, servings, instructions)
VALUES (1, 'title', 'description', 1, false, 75, 1, 4, 'Instruction 1#Instruction 2#Ins3');

INSERT INTO tbl_comment (recipe_id, user_id, comment_content)
VALUES (1, 1, 'Loved the recipe'), (1, 1, 'Did I mention i really loved it?'), (1, 2, 'Just mediocre');