package com.santander.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "profiles")
public class Profile {
    private static final long serialVersionUID = 1463423277779442808L;

    @Id
    @NotEmpty
    private String id;

    private String name;

    public Profile(String name) {
        this.name = name;

    }

}
