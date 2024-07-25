package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class 지라이슈_엔티티_삭제처리 {


    public static 지라이슈_엔티티 지라이슈_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){
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

    private static void 지라이슈_요구사항_연결_끊기_적용(지라이슈_엔티티 지라이슈_엔티티값){
        지라이슈_엔티티값.setParentReqKey(null);
    }

    public static 지라이슈_엔티티 지라이슈_요구사항_연결_끊고_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){
        지라이슈_요구사항_연결_끊기_적용(지라이슈_엔티티값);
        return 지라이슈_삭제_적용(지라이슈_엔티티값);
    }


    public static List<지라이슈_엔티티> 지라이슈_일괄_삭제_적용(List<지라이슈_엔티티> 요구사항_서브테스크_삭제_목록값){

        요구사항_서브테스크_삭제_목록값.forEach(
            지라이슈_엔티티_삭제처리::지라이슈_삭제_적용
        );

        return 요구사항_서브테스크_삭제_목록값;

    }


}
