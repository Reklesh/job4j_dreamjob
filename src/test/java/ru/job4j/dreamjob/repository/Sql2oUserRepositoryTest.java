package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import org.sql2o.Sql2o;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM users");
            query.executeUpdate();
        }
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findByEmailAndPassword(
                "email", "password")).isNotPresent();
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(
                0, "email", "name", "password")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(
                user.getEmail(), user.getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveUserWithExistingEmailThenNotSaved() {
        sql2oUserRepository.save(new User(
                0, "email", "name1", "password1"));
        var userOptional = sql2oUserRepository.save(new User(
                0, "email", "name2", "password2"));
        var savedUserOptional = sql2oUserRepository.findByEmailAndPassword(
                "email", "password2");
        assertThat(userOptional).isNotPresent();
        assertThat(savedUserOptional).isNotPresent();
        assertThatThrownBy(() -> userOptional
                .orElseThrow(
                        () -> new RuntimeException("Пользователь с такой почтой уже существует")))
                .isInstanceOf(RuntimeException.class)
                .message().isNotEmpty();
    }
}