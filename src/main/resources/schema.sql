CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID   INTEGER AUTO_INCREMENT,
    LOGIN     VARCHAR(100) NOT NULL,
    USER_NAME VARCHAR(100),
    EMAIL     VARCHAR(100),
    BIRTHDAY  DATE,
    CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS MPA_RATING
(
    MPA_ID   INTEGER,
    MPA_NAME VARCHAR(100),
    CONSTRAINT MPA_RATING_PK PRIMARY KEY (MPA_ID)
);

CREATE TABLE IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER AUTO_INCREMENT,
    FILM_NAME    VARCHAR(100),
    DESCRIPTION  VARCHAR(100),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATING       INTEGER,
    CONSTRAINT FILMS_PK PRIMARY KEY (FILM_ID),
    CONSTRAINT FILMS_FK FOREIGN KEY (RATING) REFERENCES MPA_RATING (MPA_ID)
);

CREATE TABLE IF NOT EXISTS GENRES_LIST
(
    GENRE_ID   INTEGER,
    GENRE_NAME VARCHAR(100),
    CONSTRAINT GENRES_LIST_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS FILM_GENRES
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER,
    CONSTRAINT FILM_GENRES_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID),
    CONSTRAINT FILM_GENRES_FK_1 FOREIGN KEY (GENRE_ID) REFERENCES GENRES_LIST (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS FILM_LIKES
(
    FILM_ID INTEGER,
    USER_ID INTEGER,
    CONSTRAINT FILM_LIKES_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID),
    CONSTRAINT FILM_LIKES_FK_1 FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP_STATUS
(
    STATUS_CODE INTEGER,
    STATUS_NAME VARCHAR(100),
    CONSTRAINT FRIENDSHIP_STATUS_PK PRIMARY KEY (STATUS_CODE)
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
    USER_ID     INTEGER,
    FRIEND_ID   INTEGER,
    STATUS_CODE INTEGER,
    CONSTRAINT FRIENDS_FK FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID),
    CONSTRAINT FRIENDS_FK_1 FOREIGN KEY (FRIEND_ID) REFERENCES USERS (USER_ID),
    CONSTRAINT FRIENDS_FK_2 FOREIGN KEY (STATUS_CODE) REFERENCES FRIENDSHIP_STATUS (STATUS_CODE)
);


