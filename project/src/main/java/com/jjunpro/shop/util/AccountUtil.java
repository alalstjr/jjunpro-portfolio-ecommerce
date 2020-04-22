package com.jjunpro.shop.util;

import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.security.context.AccountContext;
import com.jjunpro.shop.service.AccountServiceImpl;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountUtil {

    private final AccountServiceImpl accountService;

    /*
     * 일반 가입 : age => 19 or 27 or 30 ...
     * Kakao, Naver : age => 10 ~ 19 , birthday => 0721
     * FaceBook : age => AGE_21_PLUS, birthday => 07/21/1994
     * Google : age => TWENTY_ONE_OR_OLDER, birthday: 19940721
     */
    public byte ageConverter(String ageRange, String birthday) {
        /* ageRange 값이 숫자인지 글자인지 파악합니다. */
        try {
            int parseAge = Integer.parseInt(ageRange.substring(0, 2));

            /* Kakao or Naver 경우 생일만 지원해주고 탄생 년도를 지원 안해주므로 나이값을 기준으로 분류합니다. */
            return this.ageClassification(parseAge);
        } catch (Exception e) {
            if (ageRange != null) {
                if (ageRange.equals("AGE_13_17") || ageRange.equals("AGE_18_20") || ageRange
                        .equals("LESS_THAN_EIGHTEEN") || ageRange.equals("EIGHTEEN_TO_TWENTY")) {
                    return Byte.parseByte("10");
                } else if (ageRange.equals("AGE_21_PLUS") || ageRange
                        .equals("TWENTY_ONE_OR_OLDER")) {
                    /* FaceBook or Google 경우 나이값을 21세 까지만 지원해주므로 생년월일을 참고하여 나이값을 구한다. */
                    int userAge = this.getUserAge(birthday);

                    return this.ageClassification(userAge);
                }
            } else {
                /* Account 나이값이  */
                int userAge = this.getUserAge(birthday);

                return this.ageClassification(userAge);
            }
        }

        return Byte.parseByte("0");
    }

    private int getUserAge(String birthday) {
        DateTime dateTime = new DateTime();
        int      now      = dateTime.getYear();
        int      userAge  = 0;

        if (birthday != null && !birthday.isEmpty()) {
            if (birthday.indexOf('/') != -1) {
                String[] split = birthday.split("/");
                userAge = now - Integer.parseInt(split[2]);
            } else {
                userAge = now - Integer.parseInt(birthday.substring(0, 4));
            }
        }

        return userAge;
    }

    private byte ageClassification(int userAge) {
        if (userAge == 0) {
            return Byte.parseByte("0");
        } else if (userAge >= 10 && userAge <= 19) {
            return Byte.parseByte("10");
        } else if (userAge >= 20 && userAge <= 29) {
            return Byte.parseByte("20");
        } else if (userAge >= 30 && userAge <= 39) {
            return Byte.parseByte("30");
        } else if (userAge >= 40 && userAge <= 49) {
            return Byte.parseByte("40");
        } else {
            return Byte.parseByte("50");
        }
    }

    /* 관리자 체크 메소드 일반 유저의 정보에 접근할 때 확인하는 권환 체크 */
    public boolean adminCheck(UserDetails principal) {
        AccountContext userDetails = (AccountContext) this.accountService
                .loadUserByUsername(principal.getUsername());

        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(UserRole.ADMIN.getValue())) {
                return true;
            }
        }

        return false;
    }
}
