package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class 지라이슈_엔티티_삭제_적용 {

    private final 지라이슈_엔티티_컬렉션 지라이슈_엔티티_컬렉션값 = new 지라이슈_엔티티_컬렉션(new ArrayList<>());

    public List<지라이슈_엔티티> 삭제할_지라이슈_엔티티_목록(){
        return 지라이슈_엔티티_컬렉션값.get지라이슈_엔티티_목록();
    }

    public 지라이슈_엔티티 지라이슈_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){

        String 이슈_삭제_년월일 = LocalDate.now()
            .minusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(지라이슈_엔티티값!=null&&지라이슈_엔티티값.isNotEmpty()){
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
        지라이슈_엔티티_컬렉션값.엔티티_목록_추가(지라이슈_삭제_적용(지라이슈_엔티티값));
    }

    public void 지라이슈_일괄_삭제_적용(List<지라이슈_엔티티> 요구사항_서브테스크_삭제_목록값){
        요구사항_서브테스크_삭제_목록값.forEach(
            this::지라이슈_삭제_적용
        );
        지라이슈_엔티티_컬렉션값.엔티티_목록_리스트_전체_추가(요구사항_서브테스크_삭제_목록값);
    }

    public void 지라이슈_데이터에_존재하지_않는_지라이슈_엔티티삭제_적용(지라이슈_엔티티_컬렉션 지라이슈_엔티티_컬렉션, 지라이슈_데이터_컬렉션 지라이슈_데이터_컬렉션){

        this.지라이슈_일괄_삭제_적용(
            지라이슈_데이터_컬렉션.지라이슈_데이터에_존재하지_않는_지라이슈_목록(지라이슈_엔티티_컬렉션).get지라이슈_엔티티_목록()
        );

    }

}
