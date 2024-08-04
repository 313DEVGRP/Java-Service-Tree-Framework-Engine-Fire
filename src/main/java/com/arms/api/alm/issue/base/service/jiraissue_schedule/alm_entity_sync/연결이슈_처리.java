package com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync;

import static com.arms.config.ApplicationContextProvider.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_streategy.이슈전략_호출;
import com.arms.api.alm.utils.지라이슈_생성;

public class 연결이슈_처리 {

	private final 지라이슈_데이터 지라이슈_데이터;
	private final 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값;

	public 연결이슈_처리(지라이슈_데이터 지라이슈_데이터,지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
		this.지라이슈_데이터 = 지라이슈_데이터;
		this.지라이슈_벌크_추가_요청값 = 지라이슈_벌크_추가_요청값;
	}

	private String[] 연결이슈_아이디_배열_가져오기(지라이슈_벌크_추가_요청 요청값) {

		Set<String> 연결이슈_아이디_세트 = new HashSet<>();

		이슈링크_가져오기(요청값, 요청값.get이슈_키(), 연결이슈_아이디_세트);

		if (연결이슈_아이디_세트.isEmpty()) {
			return new String[0];
		} else {
			return 연결이슈_아이디_세트.toArray(new String[0]);
		}
	}

	private void 이슈링크_가져오기(지라이슈_벌크_추가_요청 요청값, String 현재_이슈_키, Set<String> 연결이슈_아이디_세트) {

		List<지라이슈_데이터> 이슈링크_가져오기 =
			Optional.ofNullable(ALM_이슈링크_가져오기(요청값.get지라서버_아이디(),요청값.get이슈_키()))
				.orElse(Collections.emptyList());
		if (!이슈링크_가져오기.isEmpty()) {
			for (지라이슈_데이터 ALM_데이터 : 이슈링크_가져오기) {
				String 조회조건_아이디 = 요청값.get지라서버_아이디()+"_"+ALM_데이터.getFields().getProject().getKey()+"_"+ALM_데이터.getKey();
				연결이슈_아이디_세트.add(조회조건_아이디);
			}
		}
	}

	private List<지라이슈_데이터> ALM_이슈링크_가져오기(Long 서버아이디, String 이슈키_또는_아이디) {
		return getBean(이슈전략_호출.class).이슈링크_가져오기(서버아이디, 이슈키_또는_아이디);
	}

	public  지라이슈_엔티티 요구사항_이슈() {
		return 지라이슈_생성.ELK_데이터로_변환(
			지라이슈_벌크_추가_요청값.get지라서버_아이디()
			, 지라이슈_데이터
			, true
			, null
			, 지라이슈_벌크_추가_요청값.get제품서비스_아이디()
			, 지라이슈_벌크_추가_요청값.get제품서비스_버전들()
			, 지라이슈_벌크_추가_요청값.getCReqLink()
			, 연결이슈_아이디_배열_가져오기(지라이슈_벌크_추가_요청값)
		);
	}
}
