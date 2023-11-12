package com.arms.api.engine.services.dashboard.bar;

import com.arms.api.engine.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BarChart {

    public Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;


}
