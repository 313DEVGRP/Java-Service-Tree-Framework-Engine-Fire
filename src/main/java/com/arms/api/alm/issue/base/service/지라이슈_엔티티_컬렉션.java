package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import lombok.Getter;

import java.util.List;

@Getter
public class 지라이슈_엔티티_컬렉션 {

    private final List<지라이슈_엔티티> 지라이슈_엔티티_목록;

    public 지라이슈_엔티티_컬렉션(List<지라이슈_엔티티> 지라이슈_엔티티_목록값){
        this.지라이슈_엔티티_목록 = 지라이슈_엔티티_목록값;
    }

    public void 엔티티_목록_추가(지라이슈_엔티티 지라이슈_엔티티값){
        if (지라이슈_엔티티값 != null && 지라이슈_엔티티값.isNotEmpty()) {
            this.지라이슈_엔티티_목록.add(지라이슈_엔티티값);
        }
    }

    public void 엔티티_목록_리스트_전체_추가(List<지라이슈_엔티티> 지라이슈_엔티티_목록){
        if(지라이슈_엔티티_목록!=null){
            this.지라이슈_엔티티_목록.addAll(지라이슈_엔티티_목록);
        }
    }


}
