CREATE TABLE "films" (
  "film_id" long PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" char(200),
  "release_date" date,
  "duration" int,
  "rating" enum
);

CREATE TABLE "genre" (
  "genre_id" integer PRIMARY KEY,
  "genre_name" enam
);

CREATE TABLE "genre_film" (
  "genre_id" integer PRIMARY KEY,
  "film_id" long
);

CREATE TABLE "likes_film" (
  "mark_id" long PRIMARY KEY,
  "film_id" long,
  "user_id" long
);

CREATE TABLE "users" (
  "user_id" long PRIMARY KEY,
  "login" char(100) NOT NULL,
  "username" varchar,
  "email" varchar,
  "birthday" date
);

CREATE TABLE "friends" (
  "connection_id" long PRIMARY KEY,
  "user1_id" long,
  "user2_id" long,
  "status" enum
);

COMMENT ON COLUMN "films"."release_date" IS 'дата фильма не раньше 28.12.1895 и не равна null';

ALTER TABLE "genre_film" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("genre_id");

ALTER TABLE "genre_film" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "likes_film" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "likes_film" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "friends" ADD FOREIGN KEY ("user1_id") REFERENCES "users" ("user_id");

ALTER TABLE "friends" ADD FOREIGN KEY ("user2_id") REFERENCES "users" ("user_id");
