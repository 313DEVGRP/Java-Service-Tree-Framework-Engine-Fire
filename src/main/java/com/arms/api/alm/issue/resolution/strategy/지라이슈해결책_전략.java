package com.arms.api.alm.issue.resolution.strategy;

import com.arms.api.alm.issue.resolution.model.이슈해결책_데이터;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface 지라이슈해결책_전략 {

    List<이슈해결책_데이터> 이슈해결책_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException;

}
