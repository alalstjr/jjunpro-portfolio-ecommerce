package com.jjunpro.shop.model;

import com.jjunpro.shop.enums.UserRole;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private boolean       enabled;
    private String        email;
    private String        password;
    private String        username;
    private UserRole      userRole;
    private String        ageRange;
    private String        birthday;
    private int           gender;
    private String        postcode;
    private String        addr1;
    private String        addr2;
    private String        phoneNumber;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public String defaultCreateDate(String pattern) {
        return this.createdDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String defaultModifiedDate(String pattern) {
        return this.modifiedDate.format(DateTimeFormatter.ofPattern(pattern));
    }
}