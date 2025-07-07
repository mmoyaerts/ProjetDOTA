CREATE TABLE Heroes(

   id_heroes BIGINT,

   name VARCHAR(100),

   PRIMARY KEY(id_heroes)

);

 

CREATE TABLE users(

   id_users SERIAL,

   username VARCHAR(50),

   email VARCHAR(100),

   password_hash TEXT,

   role VARCHAR(20),

   created_at DATE,

   PRIMARY KEY(id_users),

   UNIQUE(email)

);

 

CREATE TABLE game_modes(

   id_game_modes INT,

   name VARCHAR(100),

   PRIMARY KEY(id_game_modes)

);

 

CREATE TABLE team(

   id_team INT,

   name VARCHAR(50),

   PRIMARY KEY(id_team)

);

 

CREATE TABLE matches(

   id_matches INT,

   start_time TIMESTAMP,

   duration INT,

   id_game_modes INT NOT NULL,

   in_progress BOOLEAN,

   PRIMARY KEY(id_matches),

   FOREIGN KEY(id_game_modes) REFERENCES game_modes(id_game_modes)

);

 

CREATE TABLE pro(

   id_steam INT,

   pseudo VARCHAR(50),

   id_team INT NOT NULL,

   PRIMARY KEY(id_steam),

   FOREIGN KEY(id_team) REFERENCES team(id_team)

);

 

CREATE TABLE draft(

   id_draft INT,

   is_pick BOOLEAN,

   is_radiant BOOLEAN,

   id_matches INT NOT NULL,

   id_heroes INT NOT NULL,

   PRIMARY KEY(id_draft),

   FOREIGN KEY(id_matches) REFERENCES matches(id_matches),

   FOREIGN KEY(id_heroes) REFERENCES Heroes(id_heroes)

);

 

CREATE TABLE player_match(

   id_heroes INT,

   id_matches INT,

   id_steam INT,

   win BOOLEAN,

   kill INT,

   deaths INT,

   assists INT,

   PRIMARY KEY(id_heroes, id_matches, id_steam),

   FOREIGN KEY(id_heroes) REFERENCES Heroes(id_heroes),

   FOREIGN KEY(id_matches) REFERENCES matches(id_matches),

   FOREIGN KEY(id_steam) REFERENCES pro(id_steam)

);
