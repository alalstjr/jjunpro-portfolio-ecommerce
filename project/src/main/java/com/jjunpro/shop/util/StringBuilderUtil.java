package com.jjunpro.shop.util;

import org.springframework.stereotype.Component;

@Component
public class StringBuilderUtil {

    /*
     * 하나의 DB 의 연결된 다른 id 값을 여러개 저장할때 구분을 주기위해서 ',' 양쪽으로 추가해주는 메소드
     *
     * ex) "3,4" -> ",3,4," 변환
     *
     * 서버에 저장할 때 사용
     */
    public String classifyData(String ids) {
        StringBuilder stringBuffer = new StringBuilder();
        String[]      idArr        = ids.split(",");

        int i = 0;
        for (String id : idArr) {
            if (!id.isEmpty()) {
                stringBuffer.append(",");
                stringBuffer.append(id.trim());

                if (idArr.length - 1 == i) {
                    stringBuffer.append(",");
                }
            }

            i++;
        }

        return stringBuffer.toString();
    }

    /*
     * dataClassification() 메소드에서 변환한 양쪽 끝 부분의 구분 콤마 ',' 제거하기 위한 메소드
     *
     * ex) ",3,4," -> "3,4"
     *
     * 서버에서 불러와서 처리할 때 사용
     */
    public String[] classifyUnData(String ids) {
        StringBuilder stringBuffer = new StringBuilder();
        String[]      idArr        = ids.split(",");

        int i = 0;
        for (String id : idArr) {
            if (!id.isEmpty()) {
                stringBuffer.append(id.trim());

                if (idArr.length - 1 != i) {
                    stringBuffer.append(",");
                }
            }

            i++;
        }

        return stringBuffer.toString().split(",");
    }
}
