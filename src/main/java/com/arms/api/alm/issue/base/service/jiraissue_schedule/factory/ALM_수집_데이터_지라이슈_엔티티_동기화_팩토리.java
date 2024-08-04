package com.arms.api.alm.issue.base.service.jiraissue_schedule.factory;

import static com.arms.config.ApplicationContextProvider.*;

import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync.ALM_수집_데이터_지라이슈_엔티티_저장_처리;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_streategy.이슈전략_호출;

public class ALM_수집_데이터_지라이슈_엔티티_동기화_팩토리 {

	public static ALM_수집_데이터_지라이슈_엔티티_저장_처리 ALM_수집_데이터_지라이슈_엔티티_동기화_생성(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값){

		ALM_수집_데이터_지라이슈_엔티티_저장_처리 alm_수집_데이터_지라이슈_엔티티_동기화
			= new ALM_수집_데이터_지라이슈_엔티티_저장_처리(
					지라이슈_벌크_추가_요청값
					, getBean(이슈전략_호출.class).서브테스크_가져오기(지라이슈_벌크_추가_요청값)
					, getBean(이슈전략_호출.class).이슈링크_가져오기(지라이슈_벌크_추가_요청값)
			);

		return alm_수집_데이터_지라이슈_엔티티_동기화;

	}

	public static ALM_수집_데이터_지라이슈_엔티티_저장_처리 ALM_수집_데이터_증분_지라이슈_엔티티_동기화_생성(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값){

		ALM_수집_데이터_지라이슈_엔티티_저장_처리 alm_수집_데이터_증분_지라이슈_엔티티_동기화
			= new ALM_수집_데이터_지라이슈_엔티티_저장_처리(
				지라이슈_벌크_추가_요청값
				, getBean(이슈전략_호출.class).증분서브테스크_가져오기(지라이슈_벌크_추가_요청값)
				, getBean(이슈전략_호출.class).증분이슈링크_가져오기(지라이슈_벌크_추가_요청값)
			);

		return alm_수집_데이터_증분_지라이슈_엔티티_동기화;

	}
}
