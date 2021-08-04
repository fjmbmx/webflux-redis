package com.santander.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class Customer implements Serializable {
    private Integer id;
    private String name;
    private String gender;
    private String telephone;
}