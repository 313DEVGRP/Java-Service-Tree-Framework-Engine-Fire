package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.utils.지라이슈_생성;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.arms.config.ApplicationContextProvider.getBean;


public class ALM_수집_데이터_지라이슈_엔티티_동기화 {

    private final 지라이슈_엔티티_컬렉션 지라이슈_엔티티_저장_목록 = new 지라이슈_엔티티_컬렉션(new ArrayList<>());

    private final List<지라이슈_엔티티> 지라이슈_엔티티_요구사항_하위이슈_목록;

    private final List<지라이슈_데이터> 지라이슈_데이터_하위이슈_목록;

    @Getter
    private final 지라이슈_데이터 이슈_상세정보_가져오기;

    private final 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값;


    public ALM_수집_데이터_지라이슈_엔티티_동기화(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값){

        this.지라이슈_벌크_추가_요청값 = 지라이슈_벌크_추가_요청값;

        this.지라이슈_엔티티_요구사항_하위이슈_목록 = getBean(서브테스크_조회.class).요구사항_서브테스크_검색하기(지라이슈_벌크_추가_요청값);

        this.지라이슈_데이터_하위이슈_목록 = getBean(이슈전략_호출.class).서브테스크_가져오기(지라이슈_벌크_추가_요청값);

        this.이슈_상세정보_가져오기 = getBean(이슈전략_호출.class).이슈_상세정보_가져오기(지라이슈_벌크_추가_요청값);

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
}
