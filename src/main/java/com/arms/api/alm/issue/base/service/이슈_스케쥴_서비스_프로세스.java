package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.api.alm.utils.지라이슈_생성;
import com.arms.api.util.common.constrant.index.인덱스자료;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.egovframework.javaservice.esframework.EsQuery;
import com.arms.egovframework.javaservice.esframework.filter.RangeQueryFilter;
import com.arms.egovframework.javaservice.esframework.filter.TermsQueryFilter;
import com.arms.egovframework.javaservice.esframework.model.dto.기본_검색_요청;
import com.arms.egovframework.javaservice.esframework.esquery.EsQueryBuilder;
import com.arms.egovframework.javaservice.esframework.must.TermQueryMust;
import com.arms.egovframework.javaservice.esframework.factory.creator.기본_쿼리_생성기;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("지라이슈_스케쥴_서비스")
@AllArgsConstructor
public class 이슈_스케쥴_서비스_프로세스 implements 이슈_스케쥴_서비스 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈_저장소;
    private 서브테스크_조회 서브테스크_조회;
    private 이슈전략_호출 이슈전략_호출;

    @Override
    public 지라이슈_엔티티 이슈_추가하기(지라이슈_엔티티 지라이슈_엔티티) {

        지라이슈_엔티티 결과 = 지라이슈_저장소.save(지라이슈_엔티티);
        return 결과;
    }

    @Override
    public int 대량이슈_추가하기(List<지라이슈_엔티티> 대량이슈_리스트) {

        Iterable<지라이슈_엔티티> 지라이슈s = 지라이슈_저장소.saveAll(대량이슈_리스트);
        int size = StreamSupport.stream(지라이슈s.spliterator(), false).collect(toList()).size();
        return size;
    }

    @Override
    public 지라이슈_엔티티 이슈_조회하기(String 조회조건_아이디) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("id", 조회조건_아이디)
                );
        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청(){}, esQuery).생성()).stream()
                .findFirst().orElseGet(지라이슈_엔티티::new);
    }

    @Override
    public String ALM이슈_도큐먼트삭제(String 인덱스_이름, String 도큐먼트_아이디){
        return 지라이슈_저장소.deleteDocumentById(인덱스_이름, 도큐먼트_아이디);
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
                "", 제품서비스_아이디, 제품서비스_버전들, cReqLink,"");

        return 이슈_추가하기(저장할_이슈인덱스);
    }

    @Override
    public boolean 지라이슈_인덱스백업() {
        String 현재_지라이슈인덱스 = 인덱스자료.이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "-" + currentDate;

        boolean 인덱스백업 = 지라이슈_저장소.리인덱스(현재_지라이슈인덱스, 백업_지라이슈인덱스);

        if (!인덱스백업) {
            로그.error(this.getClass().getName() + " :: 지라이슈_인덱스백업() :: 리인덱스 실패!");
            return false;
        }

        return 인덱스백업;
    }

    @Override
    public boolean 지라이슈_인덱스삭제() {
        String 현재_지라이슈인덱스 = 인덱스자료.이슈_인덱스명;

        boolean 삭제성공 = 지라이슈_저장소.인덱스삭제(현재_지라이슈인덱스);
        if (삭제성공) {
            로그.info(this.getClass().getName() + " :: 지라이슈_인덱스삭제() :: 인덱스 삭제 완료!");
        } else {
            로그.error(this.getClass().getName() + " :: 지라이슈_인덱스삭제() :: 인덱스 삭제 실패!");
        }

        return 삭제성공;
    }

    @Override
    public int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키 , String 프로젝트키_또는_아이디, Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception {

        if (지라서버_아이디 == null) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키 == null || 이슈_키.isEmpty()) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (프로젝트키_또는_아이디 == null || 프로젝트키_또는_아이디.isEmpty()) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 프로젝트키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 프로젝트키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
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

        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(
                        이슈전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키)) // 요구사항 이슈 조회
                .map(이슈 -> {
                    벌크_저장_목록.add(
                            지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈, true, null, 제품서비스_아이디, 제품서비스_버전들, cReqLink,null)
                    );
                    return 이슈;
                })
                .orElse(null);

        LocalDate 이슈_삭제일 = LocalDate.now().minusDays(1);
        DateTimeFormatter 년월일_포맷 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 이슈_삭제_년월일 = 이슈_삭제일.format(년월일_포맷);

        if (반환된_이슈 == null) {
            /**
             * ALM 서버 조회 후 반환된 데이터가 Null -> 1. 삭제되어 조회가 안 되는 경우 2. 서버에서 에러가 터진 경우
             * ES 데이터에 있는지 조회 후 암스에서 관리하지 않는 요구사항으로 처리하는 로직
             **/
            String 조회조건_아이디 = 지라서버_아이디 + "_" + 프로젝트키_또는_아이디 + "_" + 이슈_키;
            지라이슈_엔티티 조회결과 = this.이슈_조회하기(조회조건_아이디); // ES에 저장된 요구사항 도큐먼트 데이터

            if (조회결과 == null || 조회결과.getId() == null) { // ES에도 해당 정보가 없는 경우
                return 0;
            }
            지라이슈_엔티티.삭제 삭제데이터= new 지라이슈_엔티티.삭제();
            삭제데이터.setDeleted_date(이슈_삭제_년월일);
            삭제데이터.setIsDeleted(true);

            조회결과.setDeleted(삭제데이터);
            조회결과.setParentReqKey(null);
            조회결과.setConnectType(null);
            벌크_저장_목록.add(조회결과);

            try {
                List<지라이슈_엔티티> 링크드이슈_목록 = Optional.ofNullable(서브테스크_조회.요구사항_링크드이슈_검색하기(지라서버_아이디,이슈_키))// ES에 저장된 요구사항에 연결된 이슈 목록
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(링크드이슈 -> {
                            링크드이슈.setConnectType(null);
                            return 링크드이슈;
                        })
                        .collect(toList());
                벌크_저장_목록.addAll(링크드이슈_목록);

                // 하위이슈의 경우 요구사항 이슈가 삭제되었을 때 전부 삭제된다(모든 도큐먼트에 삭제 flag 추가)
                List<지라이슈_엔티티> 서브테스크_목록 = Optional.ofNullable(서브테스크_조회.요구사항_서브테스크_검색하기(지라서버_아이디,이슈_키))// ES에 저장된 하위 이슈 목록
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(서브테스크 -> {
                            지라이슈_엔티티.삭제 서브테스크_삭제데이터= new 지라이슈_엔티티.삭제();
                            서브테스크_삭제데이터.setDeleted_date(이슈_삭제_년월일);
                            서브테스크_삭제데이터.setIsDeleted(true);

                            서브테스크.setDeleted(서브테스크_삭제데이터);
                            return 서브테스크;
                        })
                        .collect(toList());
                벌크_저장_목록.addAll(서브테스크_목록);
            }
            catch (Exception e) {
                로그.error(e.getMessage());
            }
        }
        else {
            List<지라이슈_데이터> ALM이슈링크_목록 = new ArrayList<지라이슈_데이터>();
            List<지라이슈_데이터> ALM서브테스크_목록 = new ArrayList<지라이슈_데이터>();

            List<지라이슈_엔티티> es에_저장된_이슈링크_목록 = null;
            List<지라이슈_엔티티> es에_저장된_서브테스크_목록 = null;
            지라이슈_엔티티 조회결과;

            if (지라이슈_저장소.인덱스_존재_확인(인덱스자료.이슈_인덱스명)) {
                String 조회조건_아이디 = 지라서버_아이디 + "_" + 프로젝트키_또는_아이디 + "_" + 이슈_키;
                조회결과 = this.이슈_조회하기(조회조건_아이디);

                es에_저장된_서브테스크_목록 = 서브테스크_조회.요구사항_서브테스크_검색하기(지라서버_아이디,이슈_키);
                es에_저장된_이슈링크_목록 = 서브테스크_조회.요구사항_링크드이슈_검색하기(지라서버_아이디,이슈_키);
            } else {
                조회결과 = null;
            }

            Optional.ofNullable(이슈전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(서브테스크_목록 -> ALM서브테스크_목록.addAll(서브테스크_목록));

            Optional.ofNullable(이슈전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(이슈링크_목록 -> ALM이슈링크_목록.addAll(이슈링크_목록));

            // 삭제된 하위이슈 flag 처리
            if (es에_저장된_서브테스크_목록 != null && !es에_저장된_서브테스크_목록.isEmpty()){

                List<지라이슈_엔티티> 삭제된_서브테스크_목록 = 삭제된_ALM_하위이슈_ES_데이터_반환(ALM서브테스크_목록, es에_저장된_서브테스크_목록, 이슈_삭제_년월일);

                벌크_저장_목록.addAll(삭제된_서브테스크_목록);
            }
            // 삭제된 연결이슈 flag 처리
            if (es에_저장된_이슈링크_목록 != null && !es에_저장된_이슈링크_목록.isEmpty()){

                List<지라이슈_엔티티> 삭제된_링크드이슈_목록= 삭제된_ALM_이슈링크_ES_데이터_반환(ALM이슈링크_목록, es에_저장된_이슈링크_목록, 이슈_삭제_년월일);

                벌크_저장_목록.addAll(삭제된_링크드이슈_목록);
            }

            /*
             *  ALM에서 연결이슈를 조회해올 때 연결된 이슈가 요구사항 이슈인지 아닌지 확인 필요
             *  => ALM에서 조회한 데이터의 upperKey가 null인 경우 요구사항 아닌 경우 하위 이슈
             * */
            if (ALM이슈링크_목록 != null && ALM이슈링크_목록.size() >= 1) {
                List<지라이슈_엔티티> 변환된_이슈_목록 = ALM이슈링크_목록.stream().map(ALM이슈링크 -> {
                            지라이슈_엔티티 변환된_이슈 = null;
                            if(ALM이슈링크.getUpperKey() == null){  // 요구사항 연결 및 암스에서 생성하지 않은 최 상단 이슈가 조회되었을 때
                                if(조회결과!=null && Objects.equals(ALM이슈링크.getKey(), 조회결과.getParentReqKey()) ){
                                    // 해당 요구사항 정보가 이전의 연결이슈로 조회되어 이미 es에 저장된 경우
                                    조회결과.setCReqLink(cReqLink);
                                    조회결과.setIsReq(true);
                                    조회결과.setConnectType("linked");
                                    조회결과.setParentReqKey(ALM이슈링크.getKey());
                                    조회결과.setPdServiceVersions(제품서비스_버전들);
                                    조회결과.setPdServiceId(제품서비스_아이디);
                                    벌크_저장_목록.set(0,조회결과);
                                }else if(조회결과 == null){// 해당 연결된 요구사항및 최초로 es에 저장되는 경우
                                    변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, ALM이슈링크,false, 이슈_키, 제품서비스_아이디,제품서비스_버전들,cReqLink,"linked");
                                    벌크_저장_목록.add(변환된_이슈);
                                }
                            }else{ // 하위 이슈가 연결되었을 때 일반 이슈가 연결되었을 때에는 단방향으로 연결 설정한다
                                변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, ALM이슈링크,false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들, cReqLink,"linked");
                                벌크_저장_목록.add(변환된_이슈);
                            }
                            return 변환된_이슈;
                        })
                        .filter(Objects::nonNull)
                        .collect(toList());
            }
            if (ALM서브테스크_목록 != null && ALM서브테스크_목록.size() >= 1) {
                List<지라이슈_엔티티> 변환된_이슈_목록 = ALM서브테스크_목록.stream().map(ALM서브테스크 -> {
                            지라이슈_엔티티 변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, ALM서브테스크,
                                    false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들, cReqLink,"subtask");
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
    public int 증분이슈_링크드이슈_서브테스크_벌크추가(Long 지라서버_아이디, String 이슈_키 , String 프로젝트키_또는_아이디, Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception {

        if (지라서버_아이디 == null) {
            로그.error("증분이슈_링크드이슈_서브테스크_벌크추가 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_링크드이슈_서브테스크_벌크추가 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키 == null || 이슈_키.isEmpty()) {
            로그.error("증분이슈_링크드이슈_서브테스크_벌크추가 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_링크드이슈_서브테스크_벌크추가 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (프로젝트키_또는_아이디 == null || 프로젝트키_또는_아이디.isEmpty()) {
            로그.error("증분이슈_링크드이슈_서브테스크_벌크추가 Error 프로젝트_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_링크드이슈_서브테스크_벌크추가 Error 프로젝트_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
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
                        증분벌크_저장_목록.add(지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 이슈, true, "", 제품서비스_아이디, 제품서비스_버전들, cReqLink,null));
                    }
                    return 이슈;
                }).orElse(null);

        LocalDate 이슈_삭제일 = LocalDate.now().minusDays(1); // 스케줄러 동작 전날
        DateTimeFormatter 년월일_포맷 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 이슈_삭제_년월일 = 이슈_삭제일.format(년월일_포맷);

        if (반환된_이슈 == null) {
            /**
             * ALM 서버 조회 후 반환된 데이터가 Null -> 1. 삭제되어 조회가 안 되는 경우 2. 서버에서 에러가 터진 경우
             * ES 데이터에 있는지 조회 후 암스에서 관리하지 않는 요구사항으로 처리하는 로직
             **/
            String 조회조건_아이디 = 지라서버_아이디 + "_" + 프로젝트키_또는_아이디 + "_" + 이슈_키;
            지라이슈_엔티티 조회결과 = this.이슈_조회하기(조회조건_아이디);

            if (조회결과 == null || 조회결과.getId() == null) {
                return 0;
            }

            지라이슈_엔티티.삭제 삭제데이터= new 지라이슈_엔티티.삭제();
            삭제데이터.setDeleted_date(이슈_삭제_년월일);
            삭제데이터.setIsDeleted(true);

            조회결과.setDeleted(삭제데이터);
            조회결과.setParentReqKey(null);
            조회결과.setConnectType(null);
            증분벌크_저장_목록.add(조회결과);

            try {
                // 요구사항이 삭제된 경우 연결이슈는 삭제되지 않고 연결이 해제됨으로, parentKey와 connectType을 초기화
                List<지라이슈_엔티티> 링크드이슈_목록 = Optional.ofNullable(서브테스크_조회.요구사항_링크드이슈_검색하기(지라서버_아이디,이슈_키))
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(링크드이슈 -> {
                            링크드이슈.setConnectType(null);
                            return 링크드이슈;
                        })
                        .collect(toList());

                증분벌크_저장_목록.addAll(링크드이슈_목록);
                // 요구사항이 삭제된 경우 하위이슈는 관리하지 않음으로 삭제처리 진행
                List<지라이슈_엔티티> 서브테스크_목록 = Optional.ofNullable(서브테스크_조회.요구사항_서브테스크_검색하기(지라서버_아이디,이슈_키))
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(서브테스크 -> {
                            서브테스크.setConnectType("subtask");

                            지라이슈_엔티티.삭제 서브테스크_삭제데이터= new 지라이슈_엔티티.삭제();
                            서브테스크_삭제데이터.setDeleted_date(이슈_삭제_년월일);
                            서브테스크_삭제데이터.setIsDeleted(true);

                            서브테스크.setDeleted(서브테스크_삭제데이터);
                            return 서브테스크;
                        })
                        .collect(toList());

                증분벌크_저장_목록.addAll(서브테스크_목록);

            } catch (Exception e) {
                로그.error(e.getMessage());
            }
        }
        else {
            // 1. 요구사항 기준 하위 이슈 및 연결이슈 조회하여 삭제된 이력 관리
            List<지라이슈_데이터> ALM서브테스크_목록 = new ArrayList<>();
            List<지라이슈_데이터> ALM이슈링크_목록 = new ArrayList<>();

            List<지라이슈_엔티티> es에_저장된_서브테스크_목록 = null;
            List<지라이슈_엔티티> es에_저장된_이슈링크_목록 = null;

            지라이슈_엔티티 조회결과;

            if (지라이슈_저장소.인덱스_존재_확인(인덱스자료.이슈_인덱스명)) {
                /* ALM에서 요구사항의 모든 하위 이슈, 연결 이슈 조회 */
                Optional.ofNullable(이슈전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키))
                        .ifPresent(서브테스크_목록 -> ALM서브테스크_목록.addAll(서브테스크_목록));

                Optional.ofNullable(이슈전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키))
                        .ifPresent(서브테스크_목록 -> ALM이슈링크_목록.addAll(서브테스크_목록));

                /* es에서 요구사항의 하위 이슈 조회*/
                es에_저장된_서브테스크_목록 = 서브테스크_조회.요구사항_서브테스크_검색하기(지라서버_아이디,이슈_키);
                es에_저장된_이슈링크_목록 = 서브테스크_조회.요구사항_링크드이슈_검색하기(지라서버_아이디,이슈_키);

                String 조회조건_아이디 = 지라서버_아이디 + "_" + 프로젝트키_또는_아이디 + "_" + 이슈_키;
                조회결과 = this.이슈_조회하기(조회조건_아이디);
            }else{
                조회결과 = null;
            }
            // 1.1. 삭제된 하위이슈 flag 처리
            if (es에_저장된_서브테스크_목록 != null && !es에_저장된_서브테스크_목록.isEmpty()){

                List<지라이슈_엔티티> 삭제된_서브테스크_목록 = 삭제된_ALM_하위이슈_ES_데이터_반환(ALM서브테스크_목록, es에_저장된_서브테스크_목록, 이슈_삭제_년월일);

                증분벌크_저장_목록.addAll(삭제된_서브테스크_목록);
            }
            // 1.2. 삭제된 연결이슈 flag 처리
            if (es에_저장된_이슈링크_목록 != null && !es에_저장된_이슈링크_목록.isEmpty()){

                List<지라이슈_엔티티> 삭제된_링크드이슈_목록= 삭제된_ALM_이슈링크_ES_데이터_반환(ALM이슈링크_목록, es에_저장된_이슈링크_목록, 이슈_삭제_년월일);

                증분벌크_저장_목록.addAll(삭제된_링크드이슈_목록);
            }

            // 2. 요구사항 기준 증분 이슈 관리
            List<지라이슈_데이터> ALM증분이슈링크_목록 = new ArrayList<>();
            List<지라이슈_데이터> ALM증분서브테스크_목록 = new ArrayList<>();
            /* ALM 업데이트된 연결된 이슈 이력 조회 */
            Optional.ofNullable(이슈전략_호출.증분이슈링크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(이슈링크_목록 -> ALM증분이슈링크_목록.addAll(이슈링크_목록));
            /* ALM 업데이트된 하위 이슈 이력 조회 */
            Optional.ofNullable(이슈전략_호출.증분서브테스크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(서브테스크_목록 -> ALM증분서브테스크_목록.addAll(서브테스크_목록));

            if (ALM증분이슈링크_목록 != null && ALM증분이슈링크_목록.size() >= 1) {
                List<지라이슈_엔티티> 변환된_이슈_목록 = ALM증분이슈링크_목록.stream().map(ALM이슈링크 -> {
                            지라이슈_엔티티 변환된_이슈 = null;
                            if(ALM이슈링크.getUpperKey() == null){  // 요구사항 연결 및 암스에서 생성하지 않은 최 상단 이슈가 조회되었을 때 (요구사항에 연결되면 무저건 업데이트가 생김)
                                if(증분벌크_저장_목록 != null && !증분벌크_저장_목록.isEmpty() &&
                                        조회결과!=null && Objects.equals(ALM이슈링크.getKey(), 조회결과.getParentReqKey())){
                                    // 해당 요구사항 정보가 이전의 연결이슈로 조회되어 이미 es에 저장된 경우
                                    조회결과.setCReqLink(cReqLink);
                                    조회결과.setIsReq(true);
                                    조회결과.setConnectType("linked");
                                    조회결과.setParentReqKey(ALM이슈링크.getKey());
                                    조회결과.setPdServiceVersions(제품서비스_버전들);
                                    조회결과.setPdServiceId(제품서비스_아이디);
                                    증분벌크_저장_목록.set(0,조회결과);
                                }else if(조회결과 == null){
                                    // 해당 연결된 요구사항및 최초로 es에 저장되는 경우
                                    변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, ALM이슈링크,false, 이슈_키, 제품서비스_아이디,제품서비스_버전들,cReqLink,"linked");
                                    증분벌크_저장_목록.add(변환된_이슈);
                                }
                            }else{ // 하위 이슈가 연결되었을 때 일반 이슈가 연결되었을 때에는 단방향으로 연결 설정한다
                                변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, ALM이슈링크,false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들, cReqLink,"linked");
                                증분벌크_저장_목록.add(변환된_이슈);
                            }
                            return 변환된_이슈;
                        })
                        .filter(Objects::nonNull)
                        .collect(toList());
            }
            // 서브 테스크
            if (ALM증분서브테스크_목록 != null && ALM증분서브테스크_목록.size() >= 1) {
                List<지라이슈_엔티티> 변환된_이슈_목록 = ALM증분서브테스크_목록.stream().map(서브테스크 -> {
                            지라이슈_엔티티 변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(지라서버_아이디, 서브테스크,false, 이슈_키, 제품서비스_아이디, 제품서비스_버전들, cReqLink,"subtask");
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

    @Override
    public int 삭제된_ALM_이슈_Document_삭제() throws Exception {
        try {
            // 스케줄러 동작일 기준 2일전
            LocalDate 스케줄러_동작일 = LocalDate.now();
            LocalDate 삭제_대상 = 스케줄러_동작일.minusDays(2);
            DateTimeFormatter 날짜형식 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String 삭제_대상일자 = 삭제_대상.format(날짜형식);
            // 해당일에 삭제된 이슈 조회
            List<지라이슈_엔티티> 삭제대상_목록 = 삭제대상이슈_조회하기(삭제_대상일자);

            if (삭제대상_목록 == null || 삭제대상_목록.isEmpty()) {
                return 0;
            }

            List<String> 삭제대상_아이디_목록 = 삭제대상_목록.stream()
                    .map(지라이슈_엔티티::getId)
                    .collect(toList());

            // 해당 이슈 증분 데이터 포함 모든 도큐먼트 일괄 삭제
            return ALM이슈_일괄_도큐먼트삭제(삭제대상_아이디_목록);

        } catch (Exception e) {
            로그.error("ALM에서 삭제된 데이터를 ES에서 삭제하는 도중 오류가 발생했습니다.", e);
            throw new Exception("ALM에서 삭제된 데이터를 ES에서 삭제하는 도중 오류가 발생했습니다.", e);
        }
    }
    private List<지라이슈_엔티티> 삭제대상이슈_조회하기(String 삭제_대상일자) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new RangeQueryFilter("deleted.deleted_date", 삭제_대상일자,삭제_대상일자,"fromto"),
                        new TermsQueryFilter("deleted.deleted_isDeleted","true")
                );
        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청(){}, esQuery).생성());
    }
    @Override
    public List<SearchHit<지라이슈_엔티티>> 모든인덱스에있는_이슈_조회하기(String 조회조건_아이디) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("id", 조회조건_아이디)
                );
        return 지라이슈_저장소.normalSearchAll(기본_쿼리_생성기.기본검색(new 기본_검색_요청(){}, esQuery).생성());
    }

    private int ALM이슈_일괄_도큐먼트삭제(List<String> 삭제대상_아이디_목록) {
        int deletedCount = 0;
        for (String 삭제대상_아이디 : 삭제대상_아이디_목록) {
            try {
                List<SearchHit<지라이슈_엔티티>> 도큐먼트_목록 = 모든인덱스에있는_이슈_조회하기(삭제대상_아이디);
                for (SearchHit<지라이슈_엔티티> 도큐먼트 : 도큐먼트_목록) {
                    ALM이슈_도큐먼트삭제(도큐먼트.getIndex(), 도큐먼트.getId());
                    deletedCount++;
                }
            } catch (Exception e) {
                로그.error("도큐먼트 일괄 삭제 중 오류 발생.", e);
            }
        }
        return deletedCount;
    }

    private List<지라이슈_엔티티> 삭제된_ALM_이슈링크_ES_데이터_반환(List<지라이슈_데이터> ALM이슈링크_목록, List<지라이슈_엔티티> es에_저장된_이슈링크_목록 , String 이슈_삭제_년월일){

        List<String> ALM에서_조회한_링크드이슈_키_목록 = ALM이슈링크_목록.stream()
                .map(지라이슈_데이터::getKey)
                .collect(toList());

        List<지라이슈_엔티티> 삭제된_링크드이슈_목록 = es에_저장된_이슈링크_목록.stream()
                .filter(지라이슈_엔티티 -> !ALM에서_조회한_링크드이슈_키_목록.contains(지라이슈_엔티티.getKey()))
                .map(지라이슈_엔티티 -> {
                    지라이슈_엔티티.setParentReqKey(null);
                    지라이슈_엔티티.setConnectType(null);
                    return 지라이슈_엔티티;
                })
                .collect(toList());

        return 삭제된_링크드이슈_목록;
    }

    private List<지라이슈_엔티티> 삭제된_ALM_하위이슈_ES_데이터_반환(List<지라이슈_데이터> ALM서브테스크_목록, List<지라이슈_엔티티> es에_저장된_서브테스크_목록, String 이슈_삭제_년월일){

        List<String> ALM에서_조회한_서브테스크_키_목록;

        // ALM서브테스크_목록이 null이거나 비어있으면 빈 리스트로 초기화
        if (ALM서브테스크_목록 == null || ALM서브테스크_목록.isEmpty()) {
            ALM에서_조회한_서브테스크_키_목록 = new ArrayList<>(); // 빈 리스트 생성
        } else {
            ALM에서_조회한_서브테스크_키_목록 = ALM서브테스크_목록.stream()
                    .map(지라이슈_데이터::getKey)
                    .collect(toList());
        }

        List<지라이슈_엔티티> 삭제된_서브테스크_목록 = es에_저장된_서브테스크_목록.stream()
                .map(지라이슈_엔티티 -> {
                    지라이슈_엔티티.삭제 삭제데이터 = 지라이슈_엔티티.getDeleted();

                    if (삭제데이터 == null) {
                        삭제데이터 = new 지라이슈_엔티티.삭제();
                    }

                    if (삭제데이터.getIsDeleted() != null && !삭제데이터.getIsDeleted()) {
                        return 지라이슈_엔티티;
                    }

                    if (ALM서브테스크_목록 == null || ALM서브테스크_목록.isEmpty() || !ALM에서_조회한_서브테스크_키_목록.contains(지라이슈_엔티티.getKey())) {
                        삭제데이터.setDeleted_date(이슈_삭제_년월일);
                        삭제데이터.setIsDeleted(true);
                        지라이슈_엔티티.setDeleted(삭제데이터);
                    }

                    return 지라이슈_엔티티;
                })
                .collect(toList());


        return 삭제된_서브테스크_목록;
    }

}