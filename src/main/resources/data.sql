merge into GENRES_LIST(GENRE_ID, GENRE_NAME)
    values (1,'Комедия');
merge into GENRES_LIST(GENRE_ID, GENRE_NAME)
    values (2,'Драма');
merge into GENRES_LIST(GENRE_ID, GENRE_NAME)
    values (3, 'Мультфильм');
merge into GENRES_LIST(GENRE_ID, GENRE_NAME)
    values (4, 'Триллер');
merge into GENRES_LIST(GENRE_ID, GENRE_NAME)
    values (5, 'Документальный');
merge into GENRES_LIST(GENRE_ID, GENRE_NAME)
    values (6, 'Боевик');


merge into MPA_RATING(MPA_ID, MPA_NAME)
    values (1, 'G');
merge into MPA_RATING(MPA_ID, MPA_NAME)
    values (2, 'PG');
merge into MPA_RATING(MPA_ID, MPA_NAME)
    values (3, 'PG-13');
merge into MPA_RATING(MPA_ID, MPA_NAME)
    values (4, 'R');
merge into MPA_RATING(MPA_ID, MPA_NAME)
    values (5, 'NC-17');

merge into FRIENDSHIP_STATUS(STATUS_CODE, STATUS_NAME)
    values (1, 'APPROVED');
merge into FRIENDSHIP_STATUS(STATUS_CODE, STATUS_NAME)
    values (2, 'NOT_APPROVED');