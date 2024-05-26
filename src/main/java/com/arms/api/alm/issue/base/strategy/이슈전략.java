package com.arms.api.alm.issue.base.strategy;

import com.arms.api.alm.issue.base.model.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.지라이슈생성_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;

import java.util.List;
import java.util.Map;

public interface 이슈전략 {

    List<지라이슈_데이터> 이슈_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_키_또는_아이디);

    지라이슈_데이터 이슈_생성하기(서버정보_데이터 서버정보, 지라이슈생성_데이터 지라이슈생성_데이터);

    Map<String,Object> 이슈_수정하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디, 지라이슈생성_데이터 지라이슈생성_데이터);

    Map<String,Object> 이슈_삭제하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디);

    지라이슈_데이터 이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디);

    List<지라이슈_데이터> 이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디);

    List<지라이슈_데이터> 서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디);

    지라이슈_데이터 증분이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디);

    List<지라이슈_데이터> 증분이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디);

    List<지라이슈_데이터> 증분서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디);
}
