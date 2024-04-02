package com.arms.api.alm.fluentd.repository;

import com.arms.api.index_entity.플루언트디_인덱스;
import com.arms.elasticsearch.repository.공통저장소;
import org.springframework.stereotype.Repository;

@Repository
public interface 플루언트디_저장소 extends 공통저장소<플루언트디_인덱스, String> {


}
