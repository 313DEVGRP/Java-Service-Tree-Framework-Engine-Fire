package com.arms.elasticsearch.services;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.arms.api.engine.jiraissue.repository.지라이슈_저장소;

@SpringBootTest
@ActiveProfiles("dev")
class 지라이슈_서비스프로세스Test {


	@Autowired
	private 지라이슈_저장소 지라이슈저장소;

	@Test
	public void testtest() throws IOException {
		// 쿼리_추상_팩토리 쿼리추상팩토리
		// 	= 지라이슈_검색_요청.builder()
		// 	.특정필드("isReq")
		// 	.특정필드검색어("true")
		// 	.그룹할필드("pdServiceVersion")
		// 	.size(1000)
		// 	.build();
		// NativeSearchQuery query = 쿼리추상팩토리.생성();;
		//
		// 검색결과_목록 버킷집계_가져오기 = 지라이슈_서비스.버킷집계_가져오기(쿼리추상팩토리);
	}
	@Test
	public void test() throws IOException {
		// 쿼리_추상_팩토리 쿼리추상팩토리
		// 	= 지라이슈_검색_서브버킷_요청.builder()
		// 		.특정필드("isReq")
		// 		.특정필드검색어("true")
		// 		.그룹할필드("pdServiceVersion")
		// 		.size(1000)
		// 		.하위_그룹할필드("assignee.assignee_emailAddress.keyword")
		// 		.build();
		// NativeSearchQuery query = 쿼리추상팩토리.생성();;
		//
		// List<검색결과> multiBucket = 지라이슈저장소.getBucket(query,
		// 	지라이슈.class,
		// 	bucket -> new 검색결과(bucket.getKeyAsString(), bucket.getDocCount(), bucket));

	}

	@Test
	public void test2() throws IOException {
		// 쿼리_추상_팩토리 쿼리추상팩토리
		// 	= 지라이슈_검색_요청.builder()
		// 	.특정필드("isReq")
		// 	.특정필드검색어("true")
		// 	.그룹할필드("pdServiceVersion")
		// 	.size(1000)
		// 	.build();
		// NativeSearchQuery query = 쿼리추상팩토리.생성();;
		// 지라이슈저장소.getBucket(query,
		// 	지라이슈.class,
		// 	bucket -> new 검색결과(bucket.getKeyAsString(), bucket.getDocCount()));
	}
}
