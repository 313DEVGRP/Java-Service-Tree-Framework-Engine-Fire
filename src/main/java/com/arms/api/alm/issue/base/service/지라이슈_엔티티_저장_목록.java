package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class 지라이슈_엔티티_저장_목록 {

    private final String 이슈_삭제_년월일 = LocalDate.now()
        .minusDays(1)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    지라이슈_엔티티 지라이슈_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){

        if(지라이슈_엔티티값!=null&&지라이슈_엔티티값.isNotEmpty()){
            지라이슈_엔티티.삭제 삭제데이터= new 지라이슈_엔티티.삭제();
            삭제데이터.setDeleted_date(이슈_삭제_년월일);
            삭제데이터.setIsDeleted(true);
            지라이슈_엔티티값.setDeleted(삭제데이터);
            지라이슈_엔티티값.setParentReqKey(null);
        }

        return 지라이슈_엔티티값;

    }

    List<지라이슈_엔티티> 서브테스크_목록_삭제_적용(List<지라이슈_엔티티> 요구사항_서브테스크_삭제_목록값){

        요구사항_서브테스크_삭제_목록값.forEach(
            서브테스크->{
                지라이슈_엔티티.삭제 서브테스크_삭제데이터= new 지라이슈_엔티티.삭제();
                서브테스크_삭제데이터.setDeleted_date(이슈_삭제_년월일);
                서브테스크_삭제데이터.setIsDeleted(true);
                서브테스크.setDeleted(서브테스크_삭제데이터);
            }
        );

        return 요구사항_서브테스크_삭제_목록값;

    }
}
