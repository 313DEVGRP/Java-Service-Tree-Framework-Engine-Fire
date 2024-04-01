package com.arms.api.engine.repository;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ActiveProfiles;

import com.arms.api.index_entity.이슈_인덱스;

@ActiveProfiles("dev")
@SpringBootTest
public class 이슈인덱스_저장소Test {


	@Autowired
	private com.arms.api.engine.jiraissue.repository.지라이슈_저장소 지라이슈_저장소;

	@Test
	public void test(){

		Long cReqLink = 10L;

		SearchHits<이슈_인덱스> ids = 지라이슈_저장소.search(
			new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.termQuery("id", "4491399083726213931_PHM_PHM-125"))
				.build());

		for (SearchHit<이슈_인덱스> id : ids) {
			이슈_인덱스 content = id.getContent();
			content.setCReqLink(cReqLink);
			지라이슈_저장소.updateSave(content,id.getIndex());
		}

	}
}
