package com.arms.api.alm.issue.type.strategy;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라API_정보;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.util.errors.에러로그_유틸;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class 클라우드_지라_이슈유형_전략 implements 이슈유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라유틸 지라유틸;
    private 지라API_정보 지라API_정보;

    @Autowired
    public 클라우드_지라_이슈유형_전략(지라유틸 지라유틸,
                                   지라API_정보 지라API_정보) {
        this.지라유틸 = 지라유틸;
        this.지라API_정보 = 지라API_정보;
    }

    @Override
    public List<이슈유형_데이터> 이슈유형_목록_가져오기(서버정보_데이터 서버정보) {

        String endpoint = "/rest/api/3/issuetype";
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        List<이슈유형_데이터> 이슈_유형_목록;
        try {
            이슈_유형_목록 = 지라유틸.get(webClient, endpoint,
                                                new ParameterizedTypeReference<List<이슈유형_데이터>>() {}).block();
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 서버정보.getConnectId() +") :: 이슈유형_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }

        if (이슈_유형_목록 == null) {
            로그.error("클라우드 전역 이슈 유형 목록이 없습니다.");
            return Collections.emptyList();
        }

        return 이슈_유형_목록;

    }

    @Override
    public List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) {

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException("프로젝트_아이디 :: " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        String endpoint = "/rest/api/3/issuetype/project?projectId=" + 프로젝트_아이디;
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        List<이슈유형_데이터> 이슈_유형_목록;
        try {
            이슈_유형_목록 = 지라유틸.get(webClient, endpoint,
                    new ParameterizedTypeReference<List<이슈유형_데이터>>() {}).block();
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 서버정보.getConnectId() +") :: 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }

        if (이슈_유형_목록 == null) {
            로그.error("클라우드 지라("+ 서버정보.getConnectId() +") :: 프로젝트 아이디 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록이 Null입니다.");
            return Collections.emptyList();
        }

        return 이슈_유형_목록;
    }

    /*public List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기2(Long 연결_아이디, String 프로젝트_아이디) {

        로그.info("클라우드 지라("+ 연결_아이디 +") :: 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기");

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            String endpoint = "/rest/api/3/issuetype/project?projectId=" + 프로젝트_아이디;

            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            List<이슈유형_데이터> 이슈_유형_목록
                    = 지라유틸.get(webClient, endpoint,
                    new ParameterizedTypeReference<List<이슈유형_데이터>>() {}).block();

            if (이슈_유형_목록 == null) {
                로그.error("클라우드 지라("+ 연결_아이디 +") :: 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록이 Null입니다.");
                return Collections.emptyList();
            }
            else if (이슈_유형_목록.size() == 0) {
                로그.error("클라우드 지라("+ 연결_아이디 +") :: 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록이 없습니다.");
                return Collections.emptyList();
            }

            List<이슈유형_데이터> 반환할_이슈_유형_목록 = new ArrayList<>();
            for(이슈유형_데이터 이슈유형 : 이슈_유형_목록) {
                Map<String, 클라우드_이슈생성필드_메타데이터.필드_메타데이터> 필드_메타데이터_목록
                        = 지라유틸.필드_메타데이터_확인하기(webClient, 프로젝트_아이디, 이슈유형.getId());

                List<String> 제거할_필드_리스트 = Arrays.asList(
                        지라API_정보.getFields().getProject(),
                        지라API_정보.getFields().getIssuetype(),
                        지라API_정보.getFields().getSummary(),
                        지라API_정보.getFields().getDescription(),
                        지라API_정보.getFields().getReporter(),
                        지라API_정보.getFields().getPriority()
                );

                제거할_필드_리스트.forEach(필드명 -> 필드_메타데이터_목록.remove(필드명));

                boolean 필수필드없음 = 필드_메타데이터_목록.entrySet().stream()
                        .noneMatch(필드 -> {
                            boolean 필수필드 = 필드.getValue().isRequired();
                            if (필수필드) {
                                로그.info("클라우드 지라("+ 연결_아이디 +") :: 프로젝트 아이디("+ 프로젝트_아이디 +") ::  이슈유형 아이디 (" + 이슈유형.getId()+
                                        ") :: {} 필드가 필수로 지정되어있습니다. A-RMS에서 지원하지 않는 필드입니다.", 필드.getKey());
                            }
                            return 필수필드;
                        });

                if(필수필드없음) {
                    반환할_이슈_유형_목록.add(이슈유형);
                }
            }

            return 반환할_이슈_유형_목록;

        } catch (Exception e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }
    }*/
}

