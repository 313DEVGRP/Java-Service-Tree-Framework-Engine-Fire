package com.arms.api.alm.issue.base.model.dto;

import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 지라이슈_데이터 {

    private String id;

    private String key;

    private String upperKey;

    private String self;

    private 지라이슈필드_데이터 fields;

    public String 조회조건_아이디(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청){
        return 지라이슈_벌크_추가_요청.get지라서버_아이디()+"_"+this.fields.getProject().getKey()+"_"+this.key;
    }


}
