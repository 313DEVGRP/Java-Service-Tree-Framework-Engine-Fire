package com.arms.api.alm.utils;

import com.arms.api.alm.issue.base.model.클라우드_이슈생성필드_메타데이터;
import com.arms.api.utils.errors.에러로그_유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class 지라유틸 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라API_정보 지라API_정보;

    public static WebClient 클라우드_통신기_생성(String uri, String email, String apiToken) {

        return WebClient.builder()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Basic " + getBase64Credentials(email, apiToken))
                .build();
    }

    public static JiraRestClient 온프레미스_통신기_생성(String jiraUrl, String jiraID, String jiraPass) throws URISyntaxException, IOException {

        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

        return factory.createWithBasicHttpAuthentication(new URI(jiraUrl), jiraID, jiraPass);

    }

    private static String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    public static <T> Mono<T> get(WebClient webClient, String uri, Class<T> responseType) {

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
    }

    public static <T> Mono<T> get(WebClient webClient, String uri, ParameterizedTypeReference<T> elementTypeRef) {

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(elementTypeRef);
    }

    public static <T> Mono<T> post(WebClient webClient, String uri, Object requestBody, Class<T> responseType) {

        return webClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType);
    }

    public static <T> Mono<T> put(WebClient webClient, String uri, Object requestBody, Class<T> responseType) {

        return webClient.put()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType);
    }

    public static <T> Mono<T> delete(WebClient webClient, String uri, Class<T> responseType) {

        return webClient.delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
    }

    public static Optional<Boolean> executePost(WebClient webClient, String uri, Object requestBody) {

        Mono<ResponseEntity<Void>> response = webClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .toEntity(Void.class);

        return response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();
    }

    public static Optional<Boolean> executePut(WebClient webClient, String uri, Object requestBody) {

        Mono<ResponseEntity<Void>> response = webClient.put()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .toEntity(Void.class);

        return response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();
    }

    public static Optional<Boolean> executeDelete(WebClient webClient, String uri) {

        Mono<ResponseEntity<Void>> response = webClient.delete()
                .uri(uri)
                .retrieve()
                .toEntity(Void.class);

        return response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();
    }

    public static LocalDateTime roundToNearest30Minutes(LocalDateTime dateTime) {
        long minutes = dateTime.getMinute();
        int remainder = (int) (minutes % 30);
        return dateTime.plusMinutes(remainder >= 30 ? 30 - remainder : -remainder);//30분 단위 리턴 시간
    }

    public Map<String, 클라우드_이슈생성필드_메타데이터.필드_메타데이터> 필드_메타데이터_확인하기(WebClient webClient, String 프로젝트_아이디, String 이슈유형_아이디) {
        String 필드확인endpoint = 지라API_정보.프로젝트키_대체하기(
                지라API_정보.getEndpoint().getCreatemeta(), 프로젝트_아이디);
        필드확인endpoint = 지라API_정보.이슈유형키_대체하기(필드확인endpoint, 이슈유형_아이디);

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        boolean isLast = false;

        List<클라우드_이슈생성필드_메타데이터.필드_메타데이터> 메타데이터_목록 = new ArrayList<>(); // 이슈 저장

        클라우드_이슈생성필드_메타데이터 클라우드_이슈생성필드_메타데이터;
        try {
            while (!isLast) {
                String endpoint = 필드확인endpoint +
                        "?startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수;

                클라우드_이슈생성필드_메타데이터
                        = 지라유틸.get(webClient, endpoint, 클라우드_이슈생성필드_메타데이터.class).block();

                if (클라우드_이슈생성필드_메타데이터 == null) {
                    로그.info("클라우드 지라 클라우드 프로젝트 : {}, 이슈유형 : {}, 이슈생성필드_메타데이터 목록이 없습니다."
                            , 프로젝트_아이디, 이슈유형_아이디);
                    return null;
                }
                else if (클라우드_이슈생성필드_메타데이터.getFields() == null || 클라우드_이슈생성필드_메타데이터.getFields().size() == 0) {
                    로그.info("클라우드 지라 클라우드 프로젝트 : {}, 이슈유형 : {}, " +
                        "이슈생성필드_메타데이터 목록이 없습니다.", 프로젝트_아이디, 이슈유형_아이디);
                    return null;
                }

                메타데이터_목록.addAll(클라우드_이슈생성필드_메타데이터.getFields());

                if (클라우드_이슈생성필드_메타데이터.getTotal() == 메타데이터_목록.size()) {
                    isLast = true;
                } else {
                    검색_시작_지점 += 최대_검색수;
                }
            }
        }
        catch (Exception e) {
            로그.error("클라우드 지라 프로젝트 : {}, 이슈유형 : {}, 이슈생성필드_메타데이터 확인하기 중 오류"
                    , 프로젝트_아이디, 이슈유형_아이디);
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(), "필드_메타데이터 확인하기");
            throw new IllegalArgumentException(에러로그 + "\n필드 메타데이터 조회 중 오류 :: 프로젝트 :: " + 프로젝트_아이디 +
                                                    " :: 이슈 유형 :: " + 이슈유형_아이디 + " :: 에러 메세지 :: " + 에러로그);
        }

        Map<String, 클라우드_이슈생성필드_메타데이터.필드_메타데이터> 필드맵 = 메타데이터_목록.stream()
                .collect(Collectors.toMap(com.arms.api.alm.issue.base.model.클라우드_이슈생성필드_메타데이터.필드_메타데이터::getFieldId, field -> field));

        return 필드맵;
    }
}
