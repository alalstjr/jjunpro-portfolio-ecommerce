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
    public String classifyData(String dataIds) {
        StringBuilder stringBuffer = new StringBuilder();
        String[]      idArr        = dataIds.split(",");

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
    public String[] classifyUnData(String dataIds) {
        StringBuilder stringBuffer = new StringBuilder();
        String[]      idArr        = dataIds.split(",");

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

    public String classifyUnDataChar(String dataId) {
        String   result = "";
        String[] idArr  = dataId.split(",");

        for (String id : idArr) {
            if (!id.isEmpty()) {
                result = id.trim();
                break;
            }
        }

        return result;
    }
}
