package com.arms.elasticsearch.services;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.test.context.ActiveProfiles;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.models.지라이슈_검색_서브버킷_요청;
import com.arms.elasticsearch.models.지라이슈_검색_요청;
import com.arms.elasticsearch.repositories.QueryAbstractFactory;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록;

@SpringBootTest
@ActiveProfiles("dev")
class 지라이슈_검색엔진Test {

	@Autowired
	private 지라이슈_서비스 지라이슈_서비스;

	@Autowired
	private 지라이슈_저장소 지라이슈저장소;

	@Test
	public void testtest() throws IOException {
		QueryAbstractFactory queryAbstractFactory
			= 지라이슈_검색_요청.builder()
			.특정필드("isReq")
			.특정필드검색어("true")
			.그룹할필드("pdServiceVersion")
			.size(1000)
			.build();
		NativeSearchQuery query = queryAbstractFactory.create();;

		검색결과_목록 버킷집계_가져오기 = 지라이슈_서비스.버킷집계_가져오기(queryAbstractFactory);
	}
	@Test
	public void test() throws IOException {
		QueryAbstractFactory queryAbstractFactory
			= 지라이슈_검색_서브버킷_요청.builder()
				.특정필드("isReq")
				.특정필드검색어("true")
				.그룹할필드("pdServiceVersion")
				.size(1000)
				.하위_그룹할필드("assignee.assignee_emailAddress.keyword")
				.build();
		NativeSearchQuery query = queryAbstractFactory.create();;

		List<검색결과> multiBucket = 지라이슈저장소.getBucket(query,
			지라이슈.class,
			bucket -> new 검색결과(bucket.getKeyAsString(), bucket.getDocCount(), bucket));

	}

	@Test
	public void test2() throws IOException {
		QueryAbstractFactory queryAbstractFactory
			= 지라이슈_검색_요청.builder()
			.특정필드("isReq")
			.특정필드검색어("true")
			.그룹할필드("pdServiceVersion")
			.size(1000)
			.build();
		NativeSearchQuery query = queryAbstractFactory.create();;
		지라이슈저장소.getBucket(query,
			지라이슈.class,
			bucket -> new 검색결과(bucket.getKeyAsString(), bucket.getDocCount()));
	}
}
