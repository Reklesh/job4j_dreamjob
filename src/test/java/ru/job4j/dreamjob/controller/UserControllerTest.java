package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegistrationPageThenGetPage() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterNewUserThenRedirectToVacanciesPage() {
        var user = new User(1, "email", "name", "password");
        when(userService.save(any())).thenReturn(Optional.of(user));
        var model = new ConcurrentModel();

        var view = userController.register(model, new User());

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenRegisterExistingUserThenGetErrorPage() {
        when(userService.save(any())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var view = userController.register(model, new User());
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void whenRequestLoginPageThenGetPage() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginWithValidCredentialsThenRedirectToVacanciesPage() {
        var email = "email";
        var password = "password";
        var user = new User(1, email, "name", password);
        var emailCaptor = ArgumentCaptor.forClass(String.class);
        var passwordCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(
                emailCaptor.capture(), passwordCaptor.capture()))
                .thenReturn(Optional.of(user));
        var model = new ConcurrentModel();
        MockHttpServletRequest request = new MockHttpServletRequest();

        var view = userController.loginUser(user, model, request);
        var actualEmail = emailCaptor.getValue();
        var actualPassword = passwordCaptor.getValue();
        var actualUser = request.getSession().getAttribute("user");

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualPassword).isEqualTo(password);
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenLoginWithInvalidCredentialsThenGetLoginPageWithError() {
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        MockHttpServletRequest request = new MockHttpServletRequest();

        var view = userController.loginUser(new User(), model, request);
        var actualMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualMessage).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenLogoutThenRedirectToLoginPage() {
        MockHttpSession session = new MockHttpSession();

        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
    }
}