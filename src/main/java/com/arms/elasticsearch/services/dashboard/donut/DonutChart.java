package com.arms.elasticsearch.services.dashboard.donut;

import com.arms.elasticsearch.models.dashboard.donut.집계_응답;

import java.io.IOException;
import java.util.List;

public interface DonutChart {

    public List<집계_응답> 이슈상태집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;
}
