package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
	UserStorage userStorage;
	FilmStorage filmStorage;
	MPAStorage mpaStorage;
	GenreStorage genreStorage;

	@Test
	void createAndFindUserByIdTest() {
		User userData = new User(0, "aaa@aaa.com", "test", "testname",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		userStorage.addUser(userData);
		User user = userStorage.getUserById(1);
		assertNotNull(user);
		assertEquals(1, user.getId());
		assertEquals("test", user.getLogin());
		assertEquals("testname", user.getName());
		assertEquals("aaa@aaa.com", user.getEmail());
		assertEquals(LocalDate.of(1992, 12, 25), user.getBirthday());
		assertEquals(0, user.getFriends().size());
	}

	@Test
	void updateUserShouldReturnUpdatedTest() {
		User userData = new User(0, "aaa@aaa.com", "test", "testname",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		userStorage.addUser(userData);
		User userDataUpdate = new User(1, "aaa@aaa.com", "testUpdate", "testnameUpdate",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		User user = userStorage.updateUser(userDataUpdate);
		assertNotNull(user);
		assertEquals(1, user.getId());
		assertEquals("testUpdate", user.getLogin());
		assertEquals("testnameUpdate", user.getName());
		assertEquals("aaa@aaa.com", user.getEmail());
		assertEquals(LocalDate.of(1992, 12, 25), user.getBirthday());
		assertEquals(0, user.getFriends().size());
	}

	@Test
	void findAllUsersTest() {
		User userData = new User(0, "aaa@aaa.com", "test", "testname",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		userStorage.addUser(userData);
		List<User> list = userStorage.getAllUsers();
		assertEquals(1, list.size());
	}

	@Test
	void addFriendShouldAddFriendTest() {
		User userData = new User(0, "aaa@aaa.com", "test", "testname",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		User userFriend = new User(0, "aaa@aaa.com", "testTwo", "testnameTwo",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		userStorage.addUser(userData);
		userStorage.addUser(userFriend);
		userStorage.addFriend(1, 2);
		assertEquals(2, userStorage.getUserById(1).getFriends().get(0));
	}

	@Test
	void deleteFriendShouldDeleteFriendTest() {
		User userData = new User(0, "aaa@aaa.com", "test", "testname",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		User userFriend = new User(0, "aaa@aaa.com", "testTwo", "testnameTwo",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		userStorage.addUser(userData);
		userStorage.addUser(userFriend);
		userStorage.addFriend(1, 2);
		userStorage.deleteFriend(1, 2);
		assertEquals(0, userStorage.getUserById(1).getFriends().size());
	}

	@Test
	void createAndFindFilmByIdTest() {
		MPA mpa = new MPA(1, "G");
		Genre genre = new Genre(1, "Комедия");
		List<Genre> genres = new ArrayList<>();
		genres.add(genre);
		Film filmData = new Film(0, "Film name", "Film description",
				LocalDate.of(1992, 12, 25), 90, new ArrayList<>(), mpa, genres);
		filmStorage.addFilm(filmData);
		Film film = filmStorage.getFilmById(1);
		assertNotNull(film);
		assertEquals("Film name", film.getName());
		assertEquals("Film description", film.getDescription());
		assertEquals(LocalDate.of(1992, 12, 25), film.getReleaseDate());
		assertEquals(90, film.getDuration());
		assertEquals(mpa, film.getMpa());
		assertEquals(genres, film.getGenres());
	}

	@Test
	void updateFilmShouldReturnUpdatedTest() {
		MPA mpa = new MPA(1, "G");
		Genre genre = new Genre(1, "Комедия");
		List<Genre> genres = new ArrayList<>();
		genres.add(genre);
		Film filmData = new Film(0, "Film name", "Film description",
				LocalDate.of(1992, 12, 25), 90, new ArrayList<>(), mpa, genres);
		Film filmDataUpdate = new Film(1, "Film name test", "Film description test",
				LocalDate.of(1992, 12, 25), 110, new ArrayList<>(), mpa, genres);
		filmStorage.addFilm(filmData);
		filmStorage.updateFilm(filmDataUpdate);
		Film film = filmStorage.getFilmById(1);
		assertNotNull(film);
		assertEquals("Film name test", film.getName());
		assertEquals("Film description test", film.getDescription());
		assertEquals(LocalDate.of(1992, 12, 25), film.getReleaseDate());
		assertEquals(110, film.getDuration());
		assertEquals(mpa, film.getMpa());
		assertEquals(genres, film.getGenres());
	}

	@Test
	void findAllFilmsTest() {
		MPA mpa = new MPA(1, "G");
		Genre genre = new Genre(1, "Комедия");
		List<Genre> genres = new ArrayList<>();
		genres.add(genre);
		Film filmData = new Film(0, "Film name", "Film description",
				LocalDate.of(1992, 12, 25), 90, new ArrayList<>(), mpa, genres);
		filmStorage.addFilm(filmData);
		List<Film> list = filmStorage.getAllFilms();
		assertEquals(1, list.size());
	}

	@Test
	void addLikeToFilmTest() {
		MPA mpa = new MPA(1, "G");
		Genre genre = new Genre(1, "Комедия");
		List<Genre> genres = new ArrayList<>();
		genres.add(genre);
		Film filmData = new Film(0, "Film name", "Film description",
				LocalDate.of(1992, 12, 25), 90, new ArrayList<>(), mpa, genres);
		filmStorage.addFilm(filmData);
		User userData = new User(0, "aaa@aaa.com", "test", "testname",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		userStorage.addUser(userData);
		filmStorage.addLike(1, 1);
		assertEquals(1, filmStorage.getFilmById(1).getLikes().get(0));
	}

	@Test
	void deleteLikeFromFilmTest() {
		MPA mpa = new MPA(1, "G");
		Genre genre = new Genre(1, "Комедия");
		List<Genre> genres = new ArrayList<>();
		genres.add(genre);
		Film filmData = new Film(0, "Film name", "Film description",
				LocalDate.of(1992, 12, 25), 90, new ArrayList<>(), mpa, genres);
		filmStorage.addFilm(filmData);
		User userData = new User(0, "aaa@aaa.com", "test", "testname",
				LocalDate.of(1992, 12, 25), new ArrayList<>());
		userStorage.addUser(userData);
		filmStorage.addLike(1, 1);
		filmStorage.deleteLike(1, 1);
		assertEquals(0, filmStorage.getFilmById(1).getLikes().size());
	}

	@Test
	void getMPAByIdTest() {
		assertEquals("PG", mpaStorage.getMPAById(2).getName());
	}

	@Test
	void getAllMPATest() {
		assertEquals(5, mpaStorage.getAllMPA().size());
	}

	@Test
	public void testGetGenreById() {
		assertEquals("Документальный", genreStorage.getGenreById(5).getName());
	}

	@Test
	public void testGetAllGenres() {
		assertEquals(6, genreStorage.getAllGenres().size());
	}
}
