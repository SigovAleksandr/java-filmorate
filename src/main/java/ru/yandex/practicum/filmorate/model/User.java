package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
public class User {

    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    @NotBlank
    private String name;
    @Past
    private Date birthday;
}
