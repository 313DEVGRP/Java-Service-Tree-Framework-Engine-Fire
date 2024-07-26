package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class 지라이슈_데이터_컬렉션 {

    private final List<지라이슈_데이터> 지라이슈_데이터_목록;

    public 지라이슈_데이터_컬렉션(List<지라이슈_데이터> 지라이슈_데이터_목록값) {
        this.지라이슈_데이터_목록 = 지라이슈_데이터_목록값;
    }

    public List<String> 지라이슈_데이터_키_목록(){
        return
            Optional.ofNullable(지라이슈_데이터_목록)
                .map(
                        data-> data.stream()
                                .map(지라이슈_데이터::getKey)
                                .collect(Collectors.toList())
                )
                .orElse(new ArrayList<>());
    }

    public 지라이슈_엔티티_컬렉션 지라이슈_데이터에_존재하지_않는_지라이슈_목록(지라이슈_엔티티_컬렉션 지라이슈_엔티티_컬렉션){
        return new 지라이슈_엔티티_컬렉션(지라이슈_엔티티_컬렉션.get지라이슈_엔티티_목록()
                .stream()
                .filter(지라이슈_엔티티->!this.지라이슈_데이터_키_목록().contains(지라이슈_엔티티.getKey()))
                .collect(Collectors.toList()));
    }

    public void 데이터_목록_추가(지라이슈_데이터 지라이슈_데이터값){

        if (지라이슈_데이터값 != null) {
            지라이슈_데이터_목록.add(지라이슈_데이터값);
        }
    }

    public void 데이터_목록_리스트_전체_추가(List<지라이슈_데이터> 지라이슈_데이터_목록){
        if(지라이슈_데이터_목록!=null){
            this.지라이슈_데이터_목록.addAll(지라이슈_데이터_목록);
        }
    }


}
