package com.arms.api.util.errors;

import com.mysql.cj.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class 에러로그_유틸 {
    private static final Logger 로그 = LoggerFactory.getLogger(LogUtils.class);

    public static void 예외로그출력(Exception e, String className, String methodName) {
        String 에러로그 = " [ " + className + " :: " + methodName + " ] :: " + e.getClass().getName() + " : " + e.getMessage();
        if (e instanceof WebClientResponseException) {
            WebClientResponseException wcException = (WebClientResponseException) e;
            HttpStatus status = wcException.getStatusCode();
            String body = wcException.getResponseBodyAsString();

            에러로그 = 에러로그 + " :: " + status + " : " + body;
        }

        로그.error(에러로그);
    }

    public static String 예외로그출력_및_반환(Exception e, String className, String methodName) {
        String 에러로그 = " [ " + className + " :: " + methodName + " ] :: " + e.getClass().getName() + " : " + e.getMessage();
        if (e instanceof WebClientResponseException) {
            WebClientResponseException wcException = (WebClientResponseException) e;
            HttpStatus status = wcException.getStatusCode();
            String body = wcException.getResponseBodyAsString();

            에러로그 = 에러로그 + " :: " + status + " : " + body;
        }

        로그.error(에러로그);
        return 에러로그;
    }
}
