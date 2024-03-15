package com.arms.api.engine.services;

import com.arms.api.engine.model.지라이슈;
import com.arms.api.engine.model.지라이슈_생성;
import com.arms.api.engine.repositories.인덱스자료;
import com.arms.api.engine.repositories.지라이슈_저장소;
import com.arms.api.engine.vo.히트맵날짜데이터;
import com.arms.api.engine.vo.히트맵데이터;
import com.arms.api.jira.jiraissue.model.지라이슈_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈필드_데이터;
import com.arms.api.jira.jiraissue.model.지라프로젝트_데이터;
import com.arms.api.jira.jiraissue.service.지라이슈_전략_호출;
import com.arms.api.jira.jiraissuestatus.model.지라이슈상태_데이터;
import com.arms.elasticsearch.util.query.EsQuery;
import com.arms.elasticsearch.util.query.EsQueryBuilder;
import com.arms.elasticsearch.util.query.bool.TermQueryMust;
import com.arms.elasticsearch.util.query.bool.TermsQueryFilter;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;
import com.arms.elasticsearch.util.검색엔진_유틸;
import com.arms.elasticsearch.util.검색조건;
import com.arms.errors.codes.에러코드;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("지라이슈_서비스")
@AllArgsConstructor
public class 지라이슈_검색엔진 implements 지라이슈_서비스{

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈저장소;

    private 지라이슈_전략_호출 지라이슈_전략_호출;

    @Override
    public 지라이슈 이슈_추가하기(지라이슈 지라이슈) {

        지라이슈 결과 = 지라이슈저장소.save(지라이슈);
        return 결과;
    }

    @Override
    public int 대량이슈_추가하기(List<지라이슈> 대량이슈_리스트) {

        Iterable<지라이슈> 지라이슈s = 지라이슈저장소.saveAll(대량이슈_리스트);
        int size = StreamSupport.stream(지라이슈s.spliterator(), false).collect(toList()).size();
        return size;
    }

    @Override
    public Iterable<지라이슈> 이슈리스트_추가하기(List<지라이슈> 지라이슈_리스트) {

        Iterable<지라이슈> 결과 = 지라이슈저장소.saveAll(지라이슈_리스트);
        return 결과;
    }

    @Override
    public 지라이슈 이슈_삭제하기(지라이슈 지라_이슈) {

        지라이슈 이슈 = 이슈_조회하기(지라_이슈.getId());
        log.info("왠만하면 쓰지 마시지...");

        if ( 이슈 == null ) {
            return null;
        }else{
            지라이슈저장소.delete(이슈);
            return 이슈;
        }
    }

    @Override
    public 지라이슈 이슈_조회하기(String 조회조건_아이디) {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.termQuery("id", 조회조건_아이디))
            .build();

        return 지라이슈저장소.normalSearch(searchQuery).stream()
                .findFirst().orElseGet(지라이슈::new);
    }

    @Override
    public List<지라이슈> 이슈_검색하기(검색조건 검색조건) {
        Query query
            = 검색엔진_유틸.buildSearchQuery(검색조건).build();
        return 지라이슈저장소.normalSearch(query);
    }

    @Override
    public 지라이슈 이슈_검색엔진_저장(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long[] 제품서비스_버전들) throws Exception {

        if (지라서버_아이디 == null) {
            로그.error("이슈_검색엔진_저장 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키 == null || 이슈_키.isEmpty()) {
            로그.error("이슈_검색엔진_저장 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (제품서비스_아이디 == null || 제품서비스_버전들 == null) {
            로그.error("이슈_검색엔진_저장 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                                        .orElse(null);

        if (반환된_이슈 == null) {
            로그.error("이슈_검색엔진_저장 Error 이슈 키에 해당하는 데이터가 없음" + 에러코드.이슈_조회_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error 이슈 키에 해당하는 데이터가 없음" + 에러코드.이슈_조회_오류.getErrorMsg());
        }

        지라이슈 저장할_지라이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true,
                                        "", 제품서비스_아이디, 제품서비스_버전들);

        return 이슈_추가하기(저장할_지라이슈);
    }

    @Override
    public boolean 지라이슈_인덱스백업() {
        String 현재_지라이슈인덱스 = 인덱스자료.지라이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "-" + currentDate;

        boolean 인덱스백업 = 지라이슈저장소.리인덱스(현재_지라이슈인덱스, 백업_지라이슈인덱스);

        if (!인덱스백업) {
            로그.error(this.getClass().getName() + " :: 지라이슈_인덱스백업() :: 리인덱스 실패!");
            return false;
        }

        return 인덱스백업;
    }

    @Override
    public boolean 지라이슈_인덱스삭제() {
        String 현재_지라이슈인덱스 = 인덱스자료.지라이슈_인덱스명;

        boolean 삭제성공 = 지라이슈저장소.인덱스삭제(현재_지라이슈인덱스);
        if (삭제성공) {
            로그.info(this.getClass().getName() + " :: 지라이슈_인덱스삭제() :: 인덱스 삭제 완료!");
        } else {
            로그.error(this.getClass().getName() + " :: 지라이슈_인덱스삭제() :: 인덱스 삭제 실패!");
        }

        return 삭제성공;
    }

    @Override
    public int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키 , Long 제품서비스_아이디, Long[] 제품서비스_버전들) throws Exception {


        if (지라서버_아이디 == null) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키 == null || 이슈_키.isEmpty()) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (제품서비스_아이디 == null || 제품서비스_버전들 == null) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        List<지라이슈> 벌크_저장_목록 = new ArrayList<지라이슈>();

        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                .map(이슈 -> {
                    벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈, true, "", 제품서비스_아이디, 제품서비스_버전들));
                    return 이슈;
                }).orElse(null);

        if (반환된_이슈 == null) {

            반환된_이슈 = new 지라이슈_데이터();
            반환된_이슈.setKey(이슈_키);

            String 프로젝트_키 = 이슈_키.substring(0, 이슈_키.indexOf("-"));

            지라프로젝트_데이터 지라프로젝트_데이터 = new 지라프로젝트_데이터();
            지라프로젝트_데이터.setKey(프로젝트_키);

            지라이슈상태_데이터 지라이슈상태_데이터 = new 지라이슈상태_데이터();
            지라이슈상태_데이터.setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");

            지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

            지라이슈필드_데이터.setProject(지라프로젝트_데이터);
            지라이슈필드_데이터.setStatus(지라이슈상태_데이터);

            반환된_이슈.setFields(지라이슈필드_데이터);

            벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true, "", 제품서비스_아이디, 제품서비스_버전들));

            try {
                List<지라이슈> 링크드이슈_서브테스크_목록 = Optional.ofNullable(요구사항_링크드이슈_서브테스크_검색하기(지라서버_아이디,
                                                                                        이슈_키, 0, 0))
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(링크드이슈_서브테스크 -> {
                            if (링크드이슈_서브테스크.getStatus() != null) {
                                링크드이슈_서브테스크.getStatus().setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                            }
                            return 링크드이슈_서브테스크;
                        })
                        .collect(toList());

                벌크_저장_목록.addAll(링크드이슈_서브테스크_목록);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            List<지라이슈_데이터> 이슈링크_또는_서브테스크_목록 = new ArrayList<지라이슈_데이터>();

            Optional.ofNullable(지라이슈_전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(이슈링크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(이슈링크_목록));

            Optional.ofNullable(지라이슈_전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(서브테스크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(서브테스크_목록));

            if (이슈링크_또는_서브테스크_목록 != null && 이슈링크_또는_서브테스크_목록.size() >= 1) {
                List<지라이슈> 변환된_이슈_목록 = 이슈링크_또는_서브테스크_목록.stream().map(이슈링크또는서브테스크 -> {
                            지라이슈 변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈링크또는서브테스크,
                                    false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들);
                            벌크_저장_목록.add(변환된_이슈);
                            return 변환된_이슈;
                        })
                        .filter(Objects::nonNull)
                        .collect(toList());
            }
        }

        return 대량이슈_추가하기(벌크_저장_목록);
    }

    @Override
    public int 증분이슈_링크드이슈_서브테스크_벌크추가(Long 지라서버_아이디, String 이슈_키 , Long 제품서비스_아이디, Long[] 제품서비스_버전들) throws Exception {

        if (지라서버_아이디 == null) {
            로그.error("증분이슈_링크드이슈_서브테스크_벌크추가 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_링크드이슈_서브테스크_벌크추가 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키 == null || 이슈_키.isEmpty()) {
            로그.error("증분이슈_링크드이슈_서브테스크_벌크추가 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_링크드이슈_서브테스크_벌크추가 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (제품서비스_아이디 == null || 제품서비스_버전들 == null) {
            로그.error("증분이슈_링크드이슈_서브테스크_벌크추가 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_링크드이슈_서브테스크_벌크추가 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        List<지라이슈> 증분벌크_저장_목록 = new ArrayList<지라이슈>();

        /**
        * 스케줄러 작동 시 암스에서 생성한 요구사항 자체가 전날 업데이트가 일어났는지 확인 시 업데이트가 없을 시 null 반환(삭제된 이슈를 조회할 때 또한)
        * 따라서 암스 생성 요구사항 상세정보를 JIRA에서 조회 후 어플리케이션 단에서 updated 항목을 검증 후 증분 데이터 판단 후 저장시키는 방법
        **/
        // 지라이슈_데이터 반환된_증분_이슈 = Optional.ofNullable(지라이슈_전략_호출.증분이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                .map(이슈 -> {
                    if (전일_업데이트여부(이슈.getFields().getUpdated())) {
                        증분벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈, true, "", 제품서비스_아이디, 제품서비스_버전들));
                    }
                    return 이슈;
                }).orElse(null);

        if (반환된_이슈 == null) {
            String 프로젝트_키 = 이슈_키.substring(0, 이슈_키.indexOf("-"));
            String 조회조건_아이디 = 지라서버_아이디 + "_" + 프로젝트_키 + "_" + 이슈_키;
            List<지라이슈> 조회결과 = ES데이터조회하기(조회조건_아이디);

            /**
            * Jira서버 조회 후 반환된 데이터가 Null -> 1. 삭제되어 조회가 안되는 경우 or 2. 에러가 터진 경우
            * ES 데이터에 있는지 조회 후 ES에 있는지 확인 후 암스에서 관리하지 않는 요구사항으로 처리하는 로직
            * jiraissue-* 인덱스 전체에서 조회해야하는 듯한 생각###
            **/
            if (조회결과 == null || 조회결과.size() == 0) {
                return 0;
            }

            반환된_이슈 = new 지라이슈_데이터();
            반환된_이슈.setKey(이슈_키);

            지라프로젝트_데이터 지라프로젝트_데이터 = new 지라프로젝트_데이터();
            지라프로젝트_데이터.setKey(프로젝트_키);

            지라이슈상태_데이터 지라이슈상태_데이터 = new 지라이슈상태_데이터();
            지라이슈상태_데이터.setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");

            지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

            지라이슈필드_데이터.setProject(지라프로젝트_데이터);
            지라이슈필드_데이터.setStatus(지라이슈상태_데이터);

            반환된_이슈.setFields(지라이슈필드_데이터);

            증분벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true, "", 제품서비스_아이디, 제품서비스_버전들));

            try {
                List<지라이슈> 링크드이슈_서브테스크_목록 = Optional.ofNullable(요구사항_링크드이슈_서브테스크_검색하기(지라서버_아이디,
                                이슈_키, 0, 0))
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(링크드이슈_서브테스크 -> {
                            if (링크드이슈_서브테스크.getStatus() != null) {
                                링크드이슈_서브테스크.getStatus().setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                            }
                            return 링크드이슈_서브테스크;
                        })
                        .collect(toList());

                증분벌크_저장_목록.addAll(링크드이슈_서브테스크_목록);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            List<지라이슈_데이터> 이슈링크_또는_서브테스크_목록 = new ArrayList<>();

            Optional.ofNullable(지라이슈_전략_호출.증분이슈링크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(이슈링크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(이슈링크_목록));

            Optional.ofNullable(지라이슈_전략_호출.증분서브테스크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(서브테스크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(서브테스크_목록));

            if (이슈링크_또는_서브테스크_목록 != null && 이슈링크_또는_서브테스크_목록.size() >= 1) {
                List<지라이슈> 변환된_이슈_목록 = 이슈링크_또는_서브테스크_목록.stream().map(이슈링크또는서브테스크 -> {
                            지라이슈 변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈링크또는서브테스크,
                                    false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들);
                            증분벌크_저장_목록.add(변환된_이슈);
                            return 변환된_이슈;
                        })
                        .filter(Objects::nonNull)
                        .collect(toList());
            }
        }

        /*
        * 대량이슈_추가하기 방어코드
        * */
        if (증분벌크_저장_목록.size() == 0) {
            return 0;
        }

        return 대량이슈_추가하기(증분벌크_저장_목록);
    }

    public boolean 전일_업데이트여부(String dateTimeStr) {

        // 가능한 날짜와 시간 형식 목록
        String[] possibleFormats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
        };

        ZonedDateTime inputDateTime = null;

        for (String format : possibleFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                inputDateTime = ZonedDateTime.parse(dateTimeStr, formatter);
                break;
            } catch (DateTimeParseException e) {
                // 날짜 형식이 일치하지 않을 시 다음 형식으로 시도
            }
        }

        if (inputDateTime == null) {
            로그.error("해당 날짜포맷은 지원하지 않는 포맷입니다.: " + dateTimeStr);
            return false;
        }

        ZonedDateTime startOfYesterday = ZonedDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endOfYesterday = startOfYesterday.withHour(23).withMinute(59).withSecond(59);

        return inputDateTime.isAfter(startOfYesterday) && inputDateTime.isBefore(endOfYesterday);
    }

    /**
     * Alias 조회 기능 추가 필요
     **/
    public List<지라이슈> ES데이터조회하기(String 조회조건_아이디) {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("id", 조회조건_아이디))
                .build();

        return 지라이슈저장소.normalSearch(searchQuery);
    }



    @Override
    public List<지라이슈> 요구사항_링크드이슈_서브테스크_검색하기(Long 서버_아이디, String 이슈_키, int 페이지_번호, int 페이지_사이즈) {
        List<String> 검색_필드 = new ArrayList<>();
        검색_필드.add("parentReqKey");

        검색조건 검색조건 = new 검색조건();
        검색조건.setFields(검색_필드);
        검색조건.setOrder(SortOrder.ASC);
        검색조건.setSearchTerm(이슈_키);
        검색조건.setPage(페이지_번호);
        검색조건.setSize(페이지_사이즈);

        Query query = 검색엔진_유틸.buildSearchQuery(검색조건,서버_아이디).build();

        return 지라이슈저장소.normalSearch(query);
    }

    @Override
    public Map<String, Long> 제품서비스_버전별_상태값_통계(Long 제품서비스_아이디, Long[] 버전_아이디들) throws IOException {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            nativeSearchQueryBuilder.withQuery(제품서비스_조회);
        }

        if ( 버전_아이디들 != null) {
            Arrays.stream(버전_아이디들).filter(버전아이디->버전아이디>9L).findAny().ifPresent(b->{
                MatchQueryBuilder 제품서비스_버전_조회 = QueryBuilders.matchQuery("pdServiceVersions", 버전_아이디들);
                nativeSearchQueryBuilder.withQuery(제품서비스_버전_조회);
            });
        }

        nativeSearchQueryBuilder.addAggregation(
                AggregationBuilders.terms("status_name_agg").field("status.status_name.keyword")
        );

        // Execute the search
        // Extract the Terms aggregation results
        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        List<검색결과> 상태값통계 = 검색결과_목록_메인.get검색결과().get("status_name_agg");

        // Iterate through the aggregation buckets

        Map<String, Long> 제품서비스_버전별_집계 = new HashMap<>();
        for (검색결과 상태값 : 상태값통계) {
            String statusName = 상태값.get필드명();
            long docCount = 상태값.get개수();
            log.info("Status Name: " + statusName + ", Count: " + docCount);

            제품서비스_버전별_집계.put(statusName, docCount);
        }

        return 제품서비스_버전별_집계;

    }

    private static LocalDateTime parseDateTime(String dateTimeStr) {

        try {
            // 온프레미스 날짜형식
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            // 클라우드 날짜형식
            DateTimeFormatter formatterWithoutColonInTimeZone =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return LocalDateTime.parse(dateTimeStr, formatterWithoutColonInTimeZone);
        }
    }

    @Override
    public Map<String, Long> 제품서비스별_담당자_요구사항_통계(Long 지라서버_아이디, Long 제품서비스_아이디, String 담당자_이메일) throws IOException {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        nativeSearchQueryBuilder.withQuery(복합조회)
                .withMaxResults(10000);

//        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build());
//        long 요구사항_개수 = 검색결과_목록_메인.get전체합계();
        long 요구사항_개수 = Optional.ofNullable(지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build()))
                .map(지라이슈 -> 지라이슈.size())
                .orElse(0);

        if ( 담당자_이메일 != null ) {
            MatchQueryBuilder 담당자_조회 = QueryBuilders.matchQuery("assignee.assignee_emailAddress.keyword", 담당자_이메일);
            복합조회.must(담당자_조회);
        }

        NativeSearchQueryBuilder aggregationQuery = new NativeSearchQueryBuilder();

        aggregationQuery.withQuery(복합조회)
                .addAggregation(AggregationBuilders.terms("상태값_집계").field("status.status_name.keyword"))
                .withMaxResults(10000);

        검색결과_목록_메인 검색결과_목록_메인_집계 = 지라이슈저장소.aggregationSearch(aggregationQuery.build());
        long 할당된_요구사항_개수 = 검색결과_목록_메인_집계.get전체합계();

        로그.info("요구사항 개수: " + 요구사항_개수);
        로그.info("할당된 요구사항 개수: " + 할당된_요구사항_개수);

        List<검색결과> 상태값_집계 = 검색결과_목록_메인_집계.get검색결과().get("상태값_집계");

        Map<String, Long> 제품서비스별_담당자_요구사항_통계 = new HashMap<>();
        제품서비스별_담당자_요구사항_통계.put("allReq", 요구사항_개수);
        제품서비스별_담당자_요구사항_통계.put("myReq", 할당된_요구사항_개수);

        for (검색결과 상태값 : 상태값_집계) {

            String 상태 = 상태값.get필드명();
            long 개수 = 상태값.get개수();
            log.info("상태값: " + 상태 + ", Count: " + 개수);

            제품서비스별_담당자_요구사항_통계.put(상태, 개수);
        }

        return 제품서비스별_담당자_요구사항_통계;
    }

    @Override
    public Map<String, Long> 제품서비스별_담당자_연관된_요구사항_통계(Long 지라서버_아이디, Long 제품서비스_아이디, String 이슈키, String 담당자_이메일) throws IOException {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();

        if ( 지라서버_아이디 != null ) {
            MatchQueryBuilder 지라서버_조회 = QueryBuilders.matchQuery("jira_server_id", 지라서버_아이디);
            복합조회.must(지라서버_조회);
        }

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        if ( 이슈키 != null ) {
            MatchQueryBuilder 요구사항_조회 = QueryBuilders.matchQuery("key", 이슈키);
            MatchQueryBuilder 하위_요구사항_조회 = QueryBuilders.matchQuery("parentReqKey", 이슈키);
            복합조회.should(요구사항_조회);
            복합조회.should(하위_요구사항_조회);
            복합조회.minimumShouldMatch(1);
        }

        nativeSearchQueryBuilder.withQuery(복합조회)
                .withMaxResults(10000);

        // aggregation 부분이 없어서 null 오류 발생
        /*long 연관된_요구사항_개수
                = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build()).get전체합계();*/

        long 연관된_요구사항_개수 = Optional.ofNullable(지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build()))
                .map(지라이슈 -> 지라이슈.size())
                .orElse(0);

        if ( 담당자_이메일 != null ) {
            MatchQueryBuilder 담당자_조회 = QueryBuilders.matchQuery("assignee.assignee_emailAddress.keyword", 담당자_이메일);
            복합조회.must(담당자_조회);
        }

        NativeSearchQueryBuilder aggregationQuery = new NativeSearchQueryBuilder();

        aggregationQuery.withQuery(복합조회)
                .addAggregation(AggregationBuilders.terms("상태값_집계").field("status.status_name.keyword"))
                .withMaxResults(10000);

        검색결과_목록_메인 검색결과_목록_메인_집계 = 지라이슈저장소.aggregationSearch(aggregationQuery.build());
        long 할당된_요구사항_개수 = 검색결과_목록_메인_집계.get전체합계();

        로그.info("연관된 요구사항 개수: " + 연관된_요구사항_개수);
        로그.info("할당된 요구사항 개수: " + 할당된_요구사항_개수);

        List<검색결과> 상태값_집계 = 검색결과_목록_메인_집계.get검색결과().get("상태값_집계");

        Map<String, Long> 제품서비스별_담당자_연관된_요구사항_통계 = new HashMap<>();
        제품서비스별_담당자_연관된_요구사항_통계.put("allReq", 연관된_요구사항_개수);
        제품서비스별_담당자_연관된_요구사항_통계.put("myReq", 할당된_요구사항_개수);

        for (검색결과 상태값 : 상태값_집계) {
            String 상태 = 상태값.get필드명();
            long 개수 = 상태값.get개수();
            log.info("상태값: " + 상태 + ", Count: " + 개수);

            제품서비스별_담당자_연관된_요구사항_통계.put(상태, 개수);
        }

        return 제품서비스별_담당자_연관된_요구사항_통계;
    }

    @Override
    public List<지라이슈> 제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks) {
        return 지라이슈저장소.findByPdServiceIdAndPdServiceVersionsIn(pdServiceLink, pdServiceVersionLinks);
    }

    @Override
    public 히트맵데이터 히트맵_제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks)
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                        .withQuery(boolQuery)
                        .withPageable(PageRequest.of(0, 10000));

        List<지라이슈> 전체결과 = new ArrayList<>();
        boolean 인덱스존재시까지  = true;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 지라인덱스 = 인덱스자료.지라이슈_인덱스명;

        while(인덱스존재시까지) {
            LocalDate 오늘일경우 = LocalDate.now();
            String 호출할_지라인덱스 = 오늘일경우.format(formatter).equals(today.format(formatter))
                                            ? 지라인덱스 : 지라인덱스 + "-" + today.format(formatter);

            if (!지라이슈저장소.인덱스_존재_확인(호출할_지라인덱스)) {
                인덱스존재시까지 = false;
                break;
            }

            today = today.minusDays(1);

            List<지라이슈> 결과 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build(), 호출할_지라인덱스);

            if (결과 != null && 결과.size() > 0) {
                전체결과.addAll(결과);
            }
        }

        히트맵데이터 히트맵데이터 = new 히트맵데이터();
        Set<String> requirementColors = new HashSet<>();
        Set<String> relationIssueColors = new HashSet<>();

        전체결과.stream().forEach(지라이슈 -> {
            if (지라이슈.getIsReq()) {
                히트맵데이터_파싱(히트맵데이터.getRequirement(), 지라이슈, requirementColors);
            } else {
                히트맵데이터_파싱(히트맵데이터.getRelationIssue(), 지라이슈, relationIssueColors);
            }
        });

        히트맵데이터.setRequirementColors(assignColors(requirementColors));
        히트맵데이터.setRelationIssueColors(assignColors(relationIssueColors));

        return 히트맵데이터;
    }

    private void 히트맵데이터_파싱(Map<String, 히트맵날짜데이터> returnObject, 지라이슈 item, Set<String> returnColors) {
        if (item.getUpdated() == null || item.getUpdated().isEmpty()) {
            로그.info(item.getKey());
            return;
        }

        String 표시날짜 = formatDate(parseDateTime(item.getUpdated()));

        if (!returnObject.containsKey(표시날짜)) {
            returnObject.put(표시날짜, new 히트맵날짜데이터());
        }

        히트맵날짜데이터 히트맵날짜데이터 = returnObject.get(표시날짜);
        히트맵날짜데이터.getContents().add(item.getSummary());
        히트맵날짜데이터.setCount(returnObject.get(표시날짜).getContents().size());
        히트맵날짜데이터.setItems(Collections.singleton(히트맵날짜데이터.getCount() + "개 업데이트"));
        returnColors.add(item.getSummary());
    }

    private static String formatDate(LocalDateTime date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return format.format(date);
    }

    private Map<String, String> assignColors(Set<String> colorsArray) {
        Map<String, String> colorsObj = new HashMap<>();
        colorsObj.put("default", "#eeeeee");

        colorsArray.forEach(item -> {
            if (!"default".equals(item)) {
                colorsObj.put(item, getRandomColor());
            }
        });

        return colorsObj;
    }

    private String getRandomColor() {
        SecureRandom random = null;

        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            로그.error("랜덤컬러 데이터 생성중 오류 : " + e.getMessage());
            throw new RuntimeException(e);
        }

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        Color randomColor = new Color(r, g, b);

        return "#" + Integer.toHexString(randomColor.getRGB()).substring(2);
    }
}
