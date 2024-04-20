package com.arms.api.alm.issue.resolution.strategy;

import com.arms.api.alm.issue.resolution.model.이슈해결책_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;

import java.util.List;

public interface 이슈해결책_전략 {

    List<이슈해결책_데이터> 이슈해결책_목록_가져오기(서버정보_데이터 서버정보);

}
