
CREATE TABLE IF NOT EXISTS users (
    user_id LONG PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR,
    email VARCHAR UNIQUE,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS mpas (
	rating_id INTEGER PRIMARY KEY AUTO_INCREMENT,
	rating_name VARCHAR(15) CHECK (rating_name IN ('G', 'PG', 'PG-13', 'R', 'NC-17', 'NONE'))
);


CREATE TABLE IF NOT EXISTS films (
	film_id LONG PRIMARY KEY AUTO_INCREMENT,
	film_name VARCHAR(100) NOT NULL,
	description VARCHAR(200),
	release_date date,
	duration int,
	rating_id int,
	FOREIGN KEY (rating_id) REFERENCES mpas(rating_id)
);

CREATE TABLE IF NOT EXISTS likes_film (
	film_id LONG,
	user_id LONG,
	PRIMARY KEY (film_id, user_id),
	FOREIGN KEY (film_id) REFERENCES films(film_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS friends (
	user_id LONG,
	friend_id LONG,
	status VARCHAR(255),
	PRIMARY KEY (user_id, friend_id),
	FOREIGN KEY (friend_id) REFERENCES users(user_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id INTEGER PRIMARY KEY AUTO_INCREMENT,
	genre_name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS genres_film (
	film_id LONG,
	genre_id INTEGER,
	PRIMARY KEY (film_id, genre_id),
	FOREIGN KEY (film_id) REFERENCES films(film_id),
	FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);



