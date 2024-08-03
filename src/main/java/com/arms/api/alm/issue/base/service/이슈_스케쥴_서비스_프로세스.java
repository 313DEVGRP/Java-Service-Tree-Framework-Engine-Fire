package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.api.util.common.constrant.index.인덱스자료;
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
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.arms.api.alm.issue.base.service.ALM_수집_데이터_지라이슈_엔티티_동기화.*;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service("지라이슈_스케쥴_서비스")
@AllArgsConstructor
@Validated
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
    public 지라이슈_엔티티 이슈_조회하기(String 조회조건_아이디){

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                    new TermQueryMust("id", 조회조건_아이디)
                );
        List<지라이슈_엔티티> 검색결과 = 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청(){}, esQuery).생성());

        if (검색결과 == null || 검색결과.isEmpty()) {
            return null;
        }

        return 검색결과.stream().findFirst().orElseGet(지라이슈_엔티티::new);
    }

    @Override
    public String ALM이슈_도큐먼트삭제(String 인덱스_이름, String 도큐먼트_아이디){
        return 지라이슈_저장소.deleteDocumentById(인덱스_이름, 도큐먼트_아이디);
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
    @Validated
    @Override
    public int 이슈_링크드이슈_서브테스크_벌크로_추가하기(@Valid 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) throws Exception {

        ALM_수집_데이터_지라이슈_엔티티_동기화 ALM_수집_데이터_지라이슈_엔티티_동기화 =  ALM_수집_데이터_지라이슈_엔티티_동기화_생성(지라이슈_벌크_추가_요청값);

        if (ALM_수집_데이터_지라이슈_엔티티_동기화.가져온_ALM_이슈_없음()) {

            /**
             * ALM 서버 조회 후 반환된 데이터가 Null -> 1. 삭제되어 조회가 안 되는 경우 2. 서버에서 에러가 터진 경우
             * ES 데이터에 있는지 조회 후 암스에서 관리하지 않는 요구사항으로 처리하는 로직
             **/

            // ES에도 해당 정보가 없는 경우
            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_요구사항_연결_끊고_삭제_적용(
                this.이슈_조회하기(지라이슈_벌크_추가_요청값.조회조건_아이디())
            );

            // 하위이슈의 경우 요구사항 이슈가 삭제되었을 때 전부 삭제된다(모든 도큐먼트에 삭제 flag 추가)
            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_일괄_삭제_적용();

        }

        if (ALM_수집_데이터_지라이슈_엔티티_동기화.가져온_ALM_이슈_있음()) {

            // 삭제된 하위이슈 flag 처리
            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_데이터에_존재하지_않는_지라이슈_엔티티_삭제_적용();

            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_엔티티_요구사항_적용();

            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_엔티티_하위이슈_목록_적용();

            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_엔티티_연결이슈_적용();

        }


        return 대량이슈_추가하기(ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_앤티티_저장할_목록_가져오기());
    }

    @Validated
    @Override
    public int 증분이슈_링크드이슈_서브테스크_벌크추가(@Valid 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) throws Exception {

        ALM_수집_데이터_지라이슈_엔티티_동기화 ALM_수집_데이터_지라이슈_엔티티_동기화 =  ALM_수집_데이터_증분_지라이슈_엔티티_동기화_생성(지라이슈_벌크_추가_요청값);

        if (ALM_수집_데이터_지라이슈_엔티티_동기화.가져온_ALM_이슈_없음()) {

            /**
             * ALM 서버 조회 후 반환된 데이터가 Null -> 1. 삭제되어 조회가 안 되는 경우 2. 서버에서 에러가 터진 경우
             * ES 데이터에 있는지 조회 후 암스에서 관리하지 않는 요구사항으로 처리하는 로직
             **/

            // ES에도 해당 정보가 없는 경우
            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_요구사항_연결_끊고_삭제_적용(
                    this.이슈_조회하기(지라이슈_벌크_추가_요청값.조회조건_아이디())
            );

            // 하위이슈의 경우 요구사항 이슈가 삭제되었을 때 전부 삭제된다(모든 도큐먼트에 삭제 flag 추가)
            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_일괄_삭제_적용();

        }

        if (ALM_수집_데이터_지라이슈_엔티티_동기화.가져온_ALM_이슈_있음()) {

            // 삭제된 하위이슈 flag 처리
            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_데이터에_존재하지_않는_지라이슈_엔티티_삭제_적용();

            ALM_수집_데이터_지라이슈_엔티티_동기화.증분_지라이슈_엔티티_요구사항_적용();

            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_엔티티_하위이슈_목록_적용();

            ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_엔티티_연결이슈_적용();
        }


        return 대량이슈_추가하기(ALM_수집_데이터_지라이슈_엔티티_동기화.지라이슈_앤티티_저장할_목록_가져오기());
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

    /*
    *   recent 필드가 true 인 es 데이터의 각 인덱스 필드를 업데이트 시킴
    * */
    @Validated
    @Override
    public int 서브테스크_상위키_필드업데이트(@Valid 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) throws Exception {

        Long 지라서버_아이디 = 지라이슈_벌크_추가_요청값.get지라서버_아이디();
        String 이슈_키 = 지라이슈_벌크_추가_요청값.get이슈_키();

        List<지라이슈_데이터> ALM_서브테스크_목록 = 이슈전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키); // ALM에서 조회한 서브테스크 목록

        List<SearchHit<지라이슈_엔티티>> ES_서브테크_이슈링크_목록 = 서브테스크_조회.요구사항_링크드이슈_서브테스크_인덱스포함_검색하기(지라서버_아이디, 이슈_키); // ES에서 조회한 서브테스크 및 이슈링크 목록

        Map<String, 지라이슈_데이터> ALM_서브테스크_데이터_맵 = Optional.ofNullable(ALM_서브테스크_목록) // ALM 서브테스크 (키, 데이터) 맵
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getKey() != null)
                .collect(Collectors.toMap(지라이슈_데이터::getKey, Function.identity()));

        List<SearchHit<지라이슈_엔티티>> 업데이트_데이터_목록 = new ArrayList<SearchHit<지라이슈_엔티티>>();

        ES_서브테크_이슈링크_목록.stream().map(서브테스크_이슈링크 ->{

            String ES_이슈_키 = 서브테스크_이슈링크.getContent().getKey();

            if (ALM_서브테스크_데이터_맵.containsKey(ES_이슈_키)) {
                // ES_서브테크_이슈링크_목록에 ALM에서 조회한 서브 테스크 가 존재한다
                // ALM의 서브 테스크 데이터에서 upperKey 조회하여 set
                String 서브테스크_상위키 = ALM_서브테스크_데이터_맵.get(ES_이슈_키).getUpperKey();

                서브테스크_이슈링크.getContent().setUpperKey(서브테스크_상위키);

                업데이트_데이터_목록.add(서브테스크_이슈링크);

            }

            return 서브테스크_이슈링크;
        }).collect(toList());

        int 업데이트된_개수 = (int) 업데이트_데이터_목록.stream()
                .filter(도큐먼트_데이터 -> {
                    try {
                        // 해당 인덱스의 도큐먼트 데이터를 업데이트 하기위함
                        지라이슈_저장소.updateSave(도큐먼트_데이터.getContent(), 도큐먼트_데이터.getIndex());
                        return true; // 성공적으로 업데이트된 경우
                    } catch (Exception e) {
                        로그.error("필드 업데이트 중 오류 발생: " + e.getMessage());
                        return false; // 오류 발생 시 false 반환
                    }
                })
                .count(); // 성공적으로 업데이트된 개수 카운트

        return 업데이트된_개수;
    }
}
