package com.arms.api.engine.common.controller;

import com.arms.api.utils.response.응답처리;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arms.api.engine.model.dto.요구사항_수정_요청;
import com.arms.api.index_entity.이슈_인덱스;
import com.arms.api.alm.issue.repository.지라이슈_저장소;

import lombok.AllArgsConstructor;

import static com.arms.api.utils.response.응답처리.success;

@RestController
@RequestMapping("/engine/jira/field/update")
@AllArgsConstructor
public class 지라이슈_필드_업데이트_컨트롤러 {

	private final 지라이슈_저장소 지라이슈_저장소;

	@PostMapping("/c_req_link")
	public ResponseEntity<응답처리.ApiResult<String>> cReqLink_수정(
			@RequestBody 요구사항_수정_요청 updateReqLinkDTOs
	) {

			SearchHits<이슈_인덱스> 지라이슈들 = 지라이슈_저장소.search(
				new NativeSearchQueryBuilder()
					.withQuery(QueryBuilders.termQuery("id", updateReqLinkDTOs.getConnectInfo()))
					.build());

			for (SearchHit<이슈_인덱스> 지라이슈 : 지라이슈들) {
				이슈_인덱스 content = 지라이슈.getContent();
				content.setCReqLink(updateReqLinkDTOs.getReqLink());
				지라이슈_저장소.updateSave(content,지라이슈.getIndex());
			}


		return ResponseEntity.ok(success("OK"));
	}



}
