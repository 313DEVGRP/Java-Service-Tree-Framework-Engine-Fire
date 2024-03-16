package com.arms.api.engine.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ActiveProfiles;

import com.arms.api.engine.model.지라이슈;
import com.arms.elasticsearch.util.custom.index.ElasticSearchIndex;

@ActiveProfiles("dev")
@SpringBootTest
public class 지라이슈_저장소Test {


	@Autowired
	private 지라이슈_저장소 지라이슈_저장소;

	@Test
	public void test(){

		Long cReqLink = 10L;

		SearchHits<지라이슈> ids = 지라이슈_저장소.search(
			new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.termQuery("id", "4491399083726213931_PHM_PHM-125"))
				.build());

		for (SearchHit<지라이슈> id : ids) {
			지라이슈 content = id.getContent();
			content.setCReqLink(cReqLink);
			지라이슈_저장소.updateSave(content,id.getIndex());
		}

	}
}
