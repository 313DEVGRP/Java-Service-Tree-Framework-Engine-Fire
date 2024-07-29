package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.utils.지라이슈_생성;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.arms.config.ApplicationContextProvider.getBean;


@Slf4j
public class ALM_수집_데이터_지라이슈_엔티티_동기화 {


    private final 지라이슈_엔티티_컬렉션 지라이슈_엔티티_저장_목록 = new 지라이슈_엔티티_컬렉션(new ArrayList<>());

    private final List<지라이슈_엔티티> 지라이슈_엔티티_요구사항_하위이슈_목록;

    private final List<지라이슈_데이터> 지라이슈_데이터_하위이슈_목록;

    private final 지라이슈_데이터 이슈_상세정보_가져오기;

    private final 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값;

    public List<지라이슈_엔티티> 지라이슈_앤티티_저장할_목록_가져오기(){
        return 지라이슈_엔티티_저장_목록.get지라이슈_엔티티_목록();
    }

    private ALM_수집_데이터_지라이슈_엔티티_동기화(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값,List<지라이슈_데이터> 지라이슈_데이터_하위이슈_목록){

        this.지라이슈_벌크_추가_요청값 = 지라이슈_벌크_추가_요청값;

        this.지라이슈_엔티티_요구사항_하위이슈_목록 = getBean(서브테스크_조회.class).요구사항_서브테스크_검색하기(지라이슈_벌크_추가_요청값);

        this.지라이슈_데이터_하위이슈_목록 = 지라이슈_데이터_하위이슈_목록;

        this.이슈_상세정보_가져오기 = getBean(이슈전략_호출.class).이슈_상세정보_가져오기(지라이슈_벌크_추가_요청값);

    }

    public static ALM_수집_데이터_지라이슈_엔티티_동기화  ALM_수집_데이터_지라이슈_엔티티_동기화_생성(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값){

        return new ALM_수집_데이터_지라이슈_엔티티_동기화(지라이슈_벌크_추가_요청값,getBean(이슈전략_호출.class).서브테스크_가져오기(지라이슈_벌크_추가_요청값));

    }

    public static ALM_수집_데이터_지라이슈_엔티티_동기화  ALM_수집_데이터_증분_지라이슈_엔티티_동기화_생성(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값){

        return new ALM_수집_데이터_지라이슈_엔티티_동기화(지라이슈_벌크_추가_요청값,getBean(이슈전략_호출.class).증분서브테스크_가져오기(지라이슈_벌크_추가_요청값));

    }

    public boolean ALM_이슈_정보_없음(){
        return 이슈_상세정보_가져오기==null;
    }

    public boolean ALM_이슈_정보_존재(){
        return !ALM_이슈_정보_없음();
    }

    public 지라이슈_엔티티 지라이슈_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){

        String 이슈_삭제_년월일 = LocalDate.now()
            .minusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(지라이슈_엔티티값!=null&&지라이슈_엔티티값.izNotEmpty()){
            지라이슈_엔티티.삭제 삭제데이터 = 지라이슈_엔티티값.getDeleted();
            if (삭제데이터 == null) {
                삭제데이터 = new 지라이슈_엔티티.삭제();
            }
            삭제데이터.setDeleted_date(이슈_삭제_년월일);
            삭제데이터.setIsDeleted(true);
            지라이슈_엔티티값.setDeleted(삭제데이터);
        }

        return 지라이슈_엔티티값;

    }

    private void 지라이슈_요구사항_연결_끊기_적용(지라이슈_엔티티 지라이슈_엔티티값){
        지라이슈_엔티티값.setParentReqKey(null);
    }

    public void 지라이슈_요구사항_연결_끊고_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){
        지라이슈_요구사항_연결_끊기_적용(지라이슈_엔티티값);
        지라이슈_엔티티_저장_목록.엔티티_목록_추가(지라이슈_삭제_적용(지라이슈_엔티티값));
    }

    public void 지라이슈_일괄_삭제_적용(){
        지라이슈_엔티티_요구사항_하위이슈_목록.forEach(
            this::지라이슈_삭제_적용
        );
        지라이슈_엔티티_저장_목록.엔티티_목록_리스트_전체_추가(지라이슈_엔티티_요구사항_하위이슈_목록);
    }

    private void 지라이슈_일괄_삭제_적용(List<지라이슈_엔티티> 요구사항_서브테스크_삭제_목록값){
        요구사항_서브테스크_삭제_목록값.forEach(
            this::지라이슈_삭제_적용
        );
        지라이슈_엔티티_저장_목록.엔티티_목록_리스트_전체_추가(요구사항_서브테스크_삭제_목록값);
    }

    public void 지라이슈_데이터에_존재하지_않는_지라이슈_엔티티_삭제_적용(){
        지라이슈_데이터_컬렉션 지라이슈_데이터_컬렉션 = new 지라이슈_데이터_컬렉션(지라이슈_데이터_하위이슈_목록);
        지라이슈_엔티티_컬렉션 지라이슈_엔티티_컬렉션 = new 지라이슈_엔티티_컬렉션(지라이슈_엔티티_요구사항_하위이슈_목록);
        this.지라이슈_일괄_삭제_적용(
            지라이슈_데이터_컬렉션.지라이슈_데이터에_존재하지_않는_지라이슈_목록(지라이슈_엔티티_컬렉션)
                .get지라이슈_엔티티_목록()
        );
    }

    /**
     * 스케줄러 작동 시 암스에서 생성한 요구사항 자체가 전날 업데이트가 일어났는지 확인 시 업데이트가 없을 시 null 반환(삭제된 이슈를 조회할 때 또한)
     * 따라서 암스 생성 요구사항 상세정보를 JIRA에서 조회 후 어플리케이션 단에서 updated 항목을 검증 후 증분 데이터 판단 후 저장시키는 방법
     **/

    public void 증분_지라이슈_엔티티_요구사항_적용() {

        if(전일_업데이트여부(이슈_상세정보_가져오기.getFields().getUpdated())){
            this.지라이슈_엔티티_저장_목록.엔티티_목록_추가(지라이슈_생성.ELK_데이터로_변환(
                            지라이슈_벌크_추가_요청값.get지라서버_아이디()
                            , 이슈_상세정보_가져오기
                            , true
                            , null
                            , 지라이슈_벌크_추가_요청값.get제품서비스_아이디()
                            , 지라이슈_벌크_추가_요청값.get제품서비스_버전들()
                            , 지라이슈_벌크_추가_요청값.getCReqLink()
                    )
            );
        }

    }

    public void 지라이슈_엔티티_요구사항_적용() {

            this.지라이슈_엔티티_저장_목록.엔티티_목록_추가(지라이슈_생성.ELK_데이터로_변환(
                    지라이슈_벌크_추가_요청값.get지라서버_아이디()
                    , 이슈_상세정보_가져오기
                    , true
                    , null
                    , 지라이슈_벌크_추가_요청값.get제품서비스_아이디()
                    , 지라이슈_벌크_추가_요청값.get제품서비스_버전들()
                    , 지라이슈_벌크_추가_요청값.getCReqLink()
            )
        );

    }

    public void 지라이슈_엔티티_하위이슈_적용() {

        this.지라이슈_데이터_하위이슈_목록
            .forEach(ALM서브테스크 -> {
                지라이슈_엔티티 변환된_이슈 =
                        지라이슈_생성.ELK_데이터로_변환(
                            지라이슈_벌크_추가_요청값.get지라서버_아이디()
                            , ALM서브테스크
                            ,false
                            , 지라이슈_벌크_추가_요청값.get이슈_키()
                            , 지라이슈_벌크_추가_요청값.get제품서비스_아이디()
                            , 지라이슈_벌크_추가_요청값.get제품서비스_버전들()
                            , 지라이슈_벌크_추가_요청값.getCReqLink()
                        );
                지라이슈_엔티티_저장_목록.엔티티_목록_추가(변환된_이슈);
            });
    }

    private boolean 전일_업데이트여부(String dateTimeStr) {

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
            log.error("해당 날짜포맷은 지원하지 않는 포맷입니다.: " + dateTimeStr);
            return false;
        }

        ZonedDateTime startOfYesterday = ZonedDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endOfYesterday = startOfYesterday.withHour(23).withMinute(59).withSecond(59);

        return inputDateTime.isAfter(startOfYesterday) && inputDateTime.isBefore(endOfYesterday);
    }
}
