package com.jjunpro.shop.model;

import com.jjunpro.shop.enums.UserRole;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@Alias("account")
public class Account {

    private Long          id;
    private String        email;
    private String        password;
    private String        username;
    private boolean       enabled;
    private UserRole      userRole;
    private String        ageRange;
    private String        birthday;
    private int           gender;
    private String        postcode;
    private String        addr1;
    private String        addr2;
    private String        phoneNumber;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Account() {
    }

    @Builder
    public Account(Long id, String email, String password, String username,
            boolean enabled, UserRole userRole, String ageRange, String birthday, int gender,
            String postcode, String addr1, String addr2, String phoneNumber,
            LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.enabled = enabled;
        this.userRole = userRole;
        this.ageRange = ageRange;
        this.birthday = birthday;
        this.gender = gender;
        this.postcode = postcode;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.phoneNumber = phoneNumber;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

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