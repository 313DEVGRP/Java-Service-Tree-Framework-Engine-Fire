package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

public interface 이슈_스케쥴_서비스 {

    지라이슈_엔티티 이슈_추가하기(지라이슈_엔티티 지라이슈_엔티티);

    int 대량이슈_추가하기(List<지라이슈_엔티티> 대량이슈_리스트);

    지라이슈_엔티티 이슈_조회하기(String 조회조건_아이디);

    String ALM이슈_도큐먼트삭제(String 인덱스_이름, String 도큐먼트_아이디);

    boolean 지라이슈_인덱스백업();

    boolean 지라이슈_인덱스삭제();

    int 이슈_링크드이슈_서브테스크_벌크로_추가하기(@Valid 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) throws Exception;

    int 증분이슈_링크드이슈_서브테스크_벌크추가(@Valid 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) throws Exception;

    int 삭제된_ALM_이슈_Document_삭제() throws Exception;

    List<SearchHit<지라이슈_엔티티>> 모든인덱스에있는_이슈_조회하기(String 조회조건_아이디);

    int 서브테스크_상위키_필드업데이트(@Valid 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) throws Exception;



}
