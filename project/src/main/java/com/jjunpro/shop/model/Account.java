package com.jjunpro.shop.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("account")
public class Account {

    private Long   id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;

    public Account() {
    }

    public Account(String username, String email, String firstname, String lastname) {
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}