package com.arms.api.alm.issue.type.strategy;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;

import java.util.List;

public interface 이슈유형_전략 {

    List<이슈유형_데이터> 이슈유형_목록_가져오기(서버정보_데이터 서버정보);

    List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디);
}
