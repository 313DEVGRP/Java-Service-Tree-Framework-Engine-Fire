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

public class 요구사항이슈_처리 {

	private final 지라이슈_데이터 지라이슈_데이터;
	private final 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값;
	private final 연결이슈_처리 연결이슈_처리;

	public 요구사항이슈_처리(지라이슈_데이터 지라이슈_데이터,지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
		this.지라이슈_데이터 = 지라이슈_데이터;
		this.지라이슈_벌크_추가_요청값 = 지라이슈_벌크_추가_요청값;
		this.연결이슈_처리 = new 연결이슈_처리(지라이슈_벌크_추가_요청값);
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
			, 연결이슈_처리.연결이슈_아이디_배열_가져오기()
		);
	}
}
