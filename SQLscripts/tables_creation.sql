DROP TABLE IF EXISTS Films, Clients, Orders;

CREATE TABLE Films (
  film_id SERIAL,
  film_name text,
  producer text,
  release_year integer,
  cassette_total_number integer DEFAULT 0,
  disc_total_number integer DEFAULT 0,
  cassette_available_number integer DEFAULT 0,
  disc_available_number integer DEFAULT 0,
  cassette_price integer DEFAULT NULL,
  disc_price integer DEFAULT NULL,
  film_is_removed boolean DEFAULT false,
  PRIMARY KEY (film_id)
);


CREATE TABLE Clients (
  client_id SERIAL,
  client_name text,
  phone text,
  client_is_removed boolean DEFAULT false,
  PRIMARY KEY (client_id)
);


CREATE TABLE Orders (
  order_id SERIAL,
  client_id integer NOT NULL REFERENCES Clients ON DELETE RESTRICT,
  film_id integer NOT NULL REFERENCES Films ON DELETE RESTRICT,
  medium VARCHAR(32) NOT NULL,
  price integer NOT NULL,
  film_issue_date date NOT NULL,
  film_return_date date,
  PRIMARY KEY (order_id)
);
