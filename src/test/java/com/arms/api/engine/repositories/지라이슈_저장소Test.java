package com.arms.api.engine.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ActiveProfiles;

import com.arms.api.engine.models.지라이슈;

@ActiveProfiles("dev")
@SpringBootTest
public class 지라이슈_저장소Test {


	@Autowired
	private 지라이슈_저장소 지라이슈_저장소;

	@Test
	public void test(){
		지라이슈 지라이슈 = new 지라이슈();
		지라이슈.setIssueID("ARMS-31314");
		지라이슈.setSummary("롤링 인덱스 테스트용 데이터 입니다.!");
		지라이슈_저장소.save(지라이슈);


		// SearchHits<지라이슈> searchHits = 지라이슈_저장소.searchAllBy();
		// System.out.println(searchHits);
		// List<지라이슈> collect = StreamSupport
		// 	.stream(지라이슈_저장소.findAll().spliterator(), false)
		// 	.collect(Collectors.toList());

		// System.out.println(collect);

	}
}
