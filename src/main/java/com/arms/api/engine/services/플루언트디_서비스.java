package com.arms.api.engine.services;

import com.arms.api.engine.dtos.검색어_기본_검색_요청;
import com.arms.api.engine.models.플루언트디;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

public interface 플루언트디_서비스 {

    List<SearchHit<플루언트디>> 플푸언트디_검색(검색어_기본_검색_요청 검색어_기본_검색_요청);
}
