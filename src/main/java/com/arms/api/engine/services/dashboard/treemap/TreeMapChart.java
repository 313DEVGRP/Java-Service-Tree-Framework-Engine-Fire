package com.arms.api.engine.services.dashboard.treemap;

import com.arms.api.engine.models.dashboard.treemap.Worker;

import java.io.IOException;
import java.util.List;

public interface TreeMapChart {
    public List<Worker> 작업자_별_요구사항_별_관여도(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;
}
