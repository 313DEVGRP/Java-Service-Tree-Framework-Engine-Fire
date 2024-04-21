package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.지라이슈필드_데이터;
import com.arms.api.alm.issue.base.model.지라프로젝트_데이터;
import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.utils.common.constrant.index.인덱스자료;
import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.api.alm.utils.지라이슈_생성;
import com.arms.elasticsearch.query.builder.검색_쿼리_빌더;
import com.arms.elasticsearch.검색조건;
import com.arms.api.utils.errors.codes.에러코드;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("지라이슈_스케쥴_서비스")
@AllArgsConstructor
public class 이슈_스케쥴_서비스_프로세스 implements 이슈_스케쥴_서비스 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈저장소;

    private com.arms.api.utils.common.component.서브테스크_조회 서브테스크_조회;

    private 이슈전략_호출 이슈전략_호출;

    @Override
    public 지라이슈_엔티티 이슈_추가하기(지라이슈_엔티티 지라이슈_엔티티) {

        지라이슈_엔티티 결과 = 지라이슈저장소.save(지라이슈_엔티티);
        return 결과;
    }

    @Override
    public int 대량이슈_추가하기(List<지라이슈_엔티티> 대량이슈_리스트) {

        Iterable<지라이슈_엔티티> 지라이슈s = 지라이슈저장소.saveAll(대량이슈_리스트);
        int size = StreamSupport.stream(지라이슈s.spliterator(), false).collect(toList()).size();
        return size;
    }

    @Override
    public 지라이슈_엔티티 이슈_조회하기(String 조회조건_아이디) {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("id", 조회조건_아이디))
                .build();

        return 지라이슈저장소.normalSearch(searchQuery).stream()
                .findFirst().orElseGet(지라이슈_엔티티::new);
    }

    @Override
    public List<지라이슈_엔티티> 이슈_검색하기(검색조건 검색조건) {
        Query query
                = 검색_쿼리_빌더.buildSearchQuery(검색조건).build();
        return 지라이슈저장소.normalSearch(query);
    }

    @Override
    public 지라이슈_엔티티 이슈_검색엔진_저장(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception {

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

        if (cReqLink == null) {
            로그.error("이슈_검색엔진_저장 Error cReqLink " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error cReqLink " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(이슈전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                .orElse(null);

        if (반환된_이슈 == null) {
            로그.error("이슈_검색엔진_저장 Error 이슈 키에 해당하는 데이터가 없음" + 에러코드.이슈_조회_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error 이슈 키에 해당하는 데이터가 없음" + 에러코드.이슈_조회_오류.getErrorMsg());
        }

        지라이슈_엔티티 저장할_이슈인덱스 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true,
                "", 제품서비스_아이디, 제품서비스_버전들, cReqLink);

        return 이슈_추가하기(저장할_이슈인덱스);
    }

    @Override
    public boolean 지라이슈_인덱스백업() {
        String 현재_지라이슈인덱스 = 인덱스자료.이슈_인덱스명;
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
        String 현재_지라이슈인덱스 = 인덱스자료.이슈_인덱스명;

        boolean 삭제성공 = 지라이슈저장소.인덱스삭제(현재_지라이슈인덱스);
        if (삭제성공) {
            로그.info(this.getClass().getName() + " :: 지라이슈_인덱스삭제() :: 인덱스 삭제 완료!");
        } else {
            로그.error(this.getClass().getName() + " :: 지라이슈_인덱스삭제() :: 인덱스 삭제 실패!");
        }

        return 삭제성공;
    }

    @Override
    public int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키 , Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception {

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

        if (cReqLink == null) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error cReqLink " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error cReqLink " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        List<지라이슈_엔티티> 벌크_저장_목록 = new ArrayList<지라이슈_엔티티>();

        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(이슈전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                .map(이슈 -> {
                    벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈, true, "", 제품서비스_아이디, 제품서비스_버전들, cReqLink));
                    return 이슈;
                }).orElse(null);

        if (반환된_이슈 == null) {

            반환된_이슈 = new 지라이슈_데이터();
            반환된_이슈.setKey(이슈_키);

            String 프로젝트_키 = 이슈_키.substring(0, 이슈_키.indexOf("-"));

            지라프로젝트_데이터 지라프로젝트_데이터 = new 지라프로젝트_데이터();
            지라프로젝트_데이터.setKey(프로젝트_키);

            이슈상태_데이터 이슈상태_데이터 = new 이슈상태_데이터();
            이슈상태_데이터.setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            이슈상태_데이터.setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            이슈상태_데이터.setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            이슈상태_데이터.setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");

            지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

            지라이슈필드_데이터.setProject(지라프로젝트_데이터);
            지라이슈필드_데이터.setStatus(이슈상태_데이터);

            반환된_이슈.setFields(지라이슈필드_데이터);
            벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true, "", 제품서비스_아이디, 제품서비스_버전들, cReqLink));

            try {
                List<지라이슈_엔티티> 링크드이슈_서브테스크_목록 = Optional.ofNullable(서브테스크_조회.요구사항_링크드이슈_서브테스크_검색하기(지라서버_아이디,
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

            Optional.ofNullable(이슈전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(이슈링크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(이슈링크_목록));

            Optional.ofNullable(이슈전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(서브테스크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(서브테스크_목록));

            if (이슈링크_또는_서브테스크_목록 != null && 이슈링크_또는_서브테스크_목록.size() >= 1) {
                List<지라이슈_엔티티> 변환된_이슈_목록 = 이슈링크_또는_서브테스크_목록.stream().map(이슈링크또는서브테스크 -> {
                            지라이슈_엔티티 변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈링크또는서브테스크,
                                    false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들, cReqLink);
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
    public int 증분이슈_링크드이슈_서브테스크_벌크추가(Long 지라서버_아이디, String 이슈_키 , Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception {

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

        if (cReqLink == null) {
            로그.error("증분이슈_링크드이슈_서브테스크_벌크추가 Error cReqLink " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_링크드이슈_서브테스크_벌크추가 Error cReqLink " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        List<지라이슈_엔티티> 증분벌크_저장_목록 = new ArrayList<지라이슈_엔티티>();

        /**
         * 스케줄러 작동 시 암스에서 생성한 요구사항 자체가 전날 업데이트가 일어났는지 확인 시 업데이트가 없을 시 null 반환(삭제된 이슈를 조회할 때 또한)
         * 따라서 암스 생성 요구사항 상세정보를 JIRA에서 조회 후 어플리케이션 단에서 updated 항목을 검증 후 증분 데이터 판단 후 저장시키는 방법
         **/
        // 지라이슈_데이터 반환된_증분_이슈 = Optional.ofNullable(이슈전략_호출.증분이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(이슈전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                .map(이슈 -> {
                    if (전일_업데이트여부(이슈.getFields().getUpdated())) {
                        증분벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈, true, "", 제품서비스_아이디, 제품서비스_버전들, cReqLink));
                    }
                    return 이슈;
                }).orElse(null);

        if (반환된_이슈 == null) {
            String 프로젝트_키 = 이슈_키.substring(0, 이슈_키.indexOf("-"));
            String 조회조건_아이디 = 지라서버_아이디 + "_" + 프로젝트_키 + "_" + 이슈_키;
            List<지라이슈_엔티티> 조회결과 = ES데이터조회하기(조회조건_아이디);

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

            이슈상태_데이터 이슈상태_데이터 = new 이슈상태_데이터();
            이슈상태_데이터.setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            이슈상태_데이터.setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            이슈상태_데이터.setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            이슈상태_데이터.setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");

            지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

            지라이슈필드_데이터.setProject(지라프로젝트_데이터);
            지라이슈필드_데이터.setStatus(이슈상태_데이터);

            반환된_이슈.setFields(지라이슈필드_데이터);

            증분벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true, "", 제품서비스_아이디, 제품서비스_버전들, cReqLink));

            try {
                List<지라이슈_엔티티> 링크드이슈_서브테스크_목록 = Optional.ofNullable(서브테스크_조회.요구사항_링크드이슈_서브테스크_검색하기(지라서버_아이디,
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

            Optional.ofNullable(이슈전략_호출.증분이슈링크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(이슈링크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(이슈링크_목록));

            Optional.ofNullable(이슈전략_호출.증분서브테스크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(서브테스크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(서브테스크_목록));

            if (이슈링크_또는_서브테스크_목록 != null && 이슈링크_또는_서브테스크_목록.size() >= 1) {
                List<지라이슈_엔티티> 변환된_이슈_목록 = 이슈링크_또는_서브테스크_목록.stream().map(이슈링크또는서브테스크 -> {
                            지라이슈_엔티티 변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈링크또는서브테스크,
                                    false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들, cReqLink);
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
    public List<지라이슈_엔티티> ES데이터조회하기(String 조회조건_아이디) {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("id", 조회조건_아이디))
                .build();

        return 지라이슈저장소.normalSearch(searchQuery);
    }







}
