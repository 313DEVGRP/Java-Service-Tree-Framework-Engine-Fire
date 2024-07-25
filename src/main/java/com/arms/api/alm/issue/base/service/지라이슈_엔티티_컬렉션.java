package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class 지라이슈_엔티티_컬렉션 {

    List<지라이슈_엔티티> 지라이슈_엔티티_목록 = new ArrayList<>();

    public void 엔티티_목록_추가(지라이슈_엔티티 지라이슈_엔티티값){

        if (지라이슈_엔티티값 != null && 지라이슈_엔티티값.isNotEmpty()) {
            지라이슈_엔티티_목록.add(지라이슈_엔티티값);
        }
    }

    public void 엔티티_목록_리스트_전체_추가(List<지라이슈_엔티티> 서브테스크_목록){
        if(서브테스크_목록!=null){
            지라이슈_엔티티_목록.addAll(서브테스크_목록);
        }
    }

}
