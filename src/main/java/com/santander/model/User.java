package com.santander.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class User implements Serializable {
    private static final long serialVersionUID = -5251195861520123379L;


    private String id;
    private String ideUser;
    @Valid
    @NotNull
    private Profile profile;
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastName;
    @NotNull
    private String age;
    @Valid
    private String email;
    private String phone;
    private String imageUri;
    private LocalDateTime fechaCreacion;

    public User(String id, String name, Profile profile, String lastName, String age, String email) {
        this.id = id;
        this.name = name;
        this.profile = profile;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
    }
    public User(  String name, Profile profile, String lastName, String age, String email) {
         this.name = name;
        this.profile = profile;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
    }
}
