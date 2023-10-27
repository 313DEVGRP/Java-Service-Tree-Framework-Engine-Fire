package com.arms.elasticsearch.services.dashboard.sankey;

import com.arms.elasticsearch.models.dashboard.sankey.SankeyElasticSearchData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SankeyChart {
    public Map<String, List<SankeyElasticSearchData>> 제품_버전별_담당자_목록(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;
}
