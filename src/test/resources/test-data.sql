MERGE INTO mpas (rating_id, rating_name) VALUES (1, 'G');
MERGE INTO mpas (rating_id, rating_name) VALUES (2, 'PG');
MERGE INTO mpas (rating_id, rating_name) VALUES (3, 'PG-13');
MERGE INTO mpas (rating_id, rating_name) VALUES (4, 'R');
MERGE INTO mpas (rating_id, rating_name) VALUES (5, 'NC-17');

MERGE INTO genres (genre_id, genre_name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, genre_name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, genre_name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, genre_name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, genre_name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, genre_name) VALUES (6, 'Боевик');

INSERT INTO users (login, username, email, birthday)
VALUES ('alex', 'alex', 'email@email.com', '1999-03-05');
INSERT INTO users (login, username, email, birthday)
VALUES ('max', 'max33', 'emailMax@email.com', '1988-03-05');
INSERT INTO users (login, username, email, birthday)
VALUES ('Elena', 'Len', 'emailLen@email.com', '1977-03-05');
INSERT INTO users (login, username, email, birthday)
VALUES ('kim', 'search', 'KimSearch@email.com', '1966-03-05');
INSERT INTO users (login, username, email, birthday)
VALUES ('Dad', 'Strong', '234567890098765@email.com', '1990-03-05');

INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Night', 'usual description', '2001-06-06', 120, 3);
INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Night2', 'unusual', '2011-06-06', 110, 2);
INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Night3', 'description', '2001-06-01', 100, 1);
INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Night4', 'Just description', '1955-06-06', 50, 4);
INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Night5', 'Old description', '2009-06-06', 10, 5);

INSERT INTO genres_film (film_id, genre_id)
VALUES (1, 1), (1, 3), (2, 5), (3, 2), (3, 6), (3, 4);

INSERT INTO likes_film (film_id, user_id)
VALUES (1, 1), (1, 3), (2, 5), (3, 2), (3, 5), (3, 4);

INSERT INTO friends (user_id, friend_id)
VALUES (1, 5), (1, 3), (2, 5), (3, 2), (3, 5), (3, 4), (3, 1), (1, 4);