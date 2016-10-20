DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

CREATE TABLE users
(
  login text NOT NULL,
  email text,
  name text,
  password text,
  points integer,
  CONSTRAINT user_pkey PRIMARY KEY (login)
);

CREATE SEQUENCE post_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE TABLE posts
(
  post_id integer NOT NULL DEFAULT nextval('post_id_seq'::regclass),
  title text,
  content text,
  login text,
  CONSTRAINT post_pkey PRIMARY KEY (post_id),
  CONSTRAINT post_login_fkey FOREIGN KEY (login)
      REFERENCES users (login) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE comment_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE TABLE comments
(
  comment_id integer NOT NULL DEFAULT nextval('comment_id_seq'::regclass),
  content text,
  login text,
  post_id integer,
  CONSTRAINT comment_pkey PRIMARY KEY (comment_id),
  CONSTRAINT comment_post_id_fkey FOREIGN KEY (post_id)
      REFERENCES posts (post_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT comment_login_fkey FOREIGN KEY (login)
      REFERENCES users (login) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);