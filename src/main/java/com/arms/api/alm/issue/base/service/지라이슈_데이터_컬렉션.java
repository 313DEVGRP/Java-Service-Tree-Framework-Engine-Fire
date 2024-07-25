package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class 지라이슈_데이터_컬렉션 {

    private final List<지라이슈_데이터> 지라이슈_데이터_목록값 ;

    public 지라이슈_데이터_컬렉션(List<지라이슈_데이터> 지라이슈_데이터_목록값) {
        this.지라이슈_데이터_목록값 = 지라이슈_데이터_목록값;
    }

    public void 데이터_목록_추가(지라이슈_데이터 지라이슈_데이터값){

        if (지라이슈_데이터값 != null) {
            지라이슈_데이터_목록값.add(지라이슈_데이터값);
        }
    }


}
