package com.arms.api.alm.issue.status.strategy;

import com.arms.api.alm.issue.status.model.이슈상태_데이터;

import java.util.List;

public interface 이슈상태_전략 {

    List<이슈상태_데이터> 이슈상태_목록_가져오기(Long 연결_아이디) throws Exception;

    List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception;

}
