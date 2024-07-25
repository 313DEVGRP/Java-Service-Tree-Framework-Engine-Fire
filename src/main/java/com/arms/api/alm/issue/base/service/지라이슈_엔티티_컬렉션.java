package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class 지라이슈_엔티티_컬렉션 {

    private final List<지라이슈_엔티티> 지라이슈_엔티티_목록;

    public 지라이슈_엔티티_컬렉션(List<지라이슈_엔티티> 지라이슈_엔티티_목록값){
        this.지라이슈_엔티티_목록 = 지라이슈_엔티티_목록값;
    }

    public boolean isEmpty(){
        return 지라이슈_엔티티_목록!=null&&지라이슈_엔티티_목록.size()==0;
    }

    public boolean isNotEmpty(){
        return !isEmpty();
    }

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

    public List<지라이슈_엔티티> 지라이슈_엔티티에_존재하지_않는_목록_찾기(지라이슈_데이터_컬렉션 지라이슈_데이터_컬렉션){

        List<지라이슈_데이터> 지라이슈_데이터_목록값 = 지라이슈_데이터_컬렉션.get지라이슈_데이터_목록값();

        List<String> 지라이슈_데이터_키_목록값 = Optional.ofNullable(지라이슈_데이터_목록값)
            .map(data-> data.stream()
                    .map(지라이슈_데이터::getKey)
                    .collect(Collectors.toList()))
            .orElse(new ArrayList<>());

        return this.get지라이슈_엔티티_목록().stream()
            .filter(지라이슈_엔티티::삭제_여부)
            .filter(지라이슈_엔티티->!지라이슈_데이터_키_목록값.contains(지라이슈_엔티티.getKey()))
            .collect(Collectors.toList());

    }

}
