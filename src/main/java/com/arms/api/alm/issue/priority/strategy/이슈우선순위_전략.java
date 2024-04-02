package com.arms.api.alm.issue.priority.strategy;

import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;

import java.util.List;

public interface 이슈우선순위_전략 {
    List<이슈우선순위_데이터> 우선순위_목록_가져오기(Long 연결_아이디) throws Exception;
}
