package com.arms.api.alm.issue.base.service.jiraissue;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;

import java.util.List;

public interface 지라이슈_서비스 {

    List<지라이슈_엔티티>  지라이슈_조회(Long pdServiceLink, Long[] pdServiceVersionLinks);

    List<지라이슈_엔티티>  지라이슈_조회(boolean isReq, Long pdServiceLink, Long[] pdServiceVersionLinks);

    List<지라이슈_엔티티>  지라이슈_조회(List<String> parentReqKeys);
}
