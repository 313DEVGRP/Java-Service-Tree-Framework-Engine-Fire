package com.arms.api.engine.services;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ActiveProfiles;

import com.arms.api.engine.jiraissue.entity.지라이슈;
import com.arms.api.engine.jiraissue.repository.지라이슈_저장소;

@SpringBootTest
@ActiveProfiles("dev")
public class 지라이슈_서비스프로세스Test {


	@Autowired
	private 지라이슈_저장소 지라이슈저장소;

	@Test
	public void test(){
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
			.withQuery(QueryBuilders.termQuery("id", "1797187033776577721_DEMO_DEMO-21"))
			.build();

		지라이슈 지라이슈 = 지라이슈저장소.normalSearch(searchQuery).stream().findFirst().orElseGet(지라이슈::new);
		System.out.println(지라이슈);
	}
}
