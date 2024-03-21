package com.arms.api.alm.priority.strategy;

import com.arms.api.alm.priority.model.지라이슈우선순위_데이터;

import java.util.List;

public interface 지라이슈우선순위_전략 {
    List<지라이슈우선순위_데이터> 우선순위_목록_가져오기(Long 연결_아이디) throws Exception;
}
