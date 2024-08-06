package com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.service.jiraissue.지라이슈_서비스_프로세스;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync.abastract.ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.subtask_repository.서브테스크_조회;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.arms.config.ApplicationContextProvider.getBean;


@Slf4j
public class ALM_수집_데이터_공통_지라이슈_엔티티_삭제_처리 implements ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스 {

	private final 지라이슈_엔티티_컬렉션 지라이슈_엔티티_저장_목록 = new 지라이슈_엔티티_컬렉션(new ArrayList<>());
	private final 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값;

	@Override
	public List<지라이슈_엔티티> 지라이슈_앤티티_저장할_목록_가져오기(){
		return 지라이슈_엔티티_저장_목록.get지라이슈_엔티티_목록();
	}

	public ALM_수집_데이터_공통_지라이슈_엔티티_삭제_처리(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값){
		this.지라이슈_벌크_추가_요청값 = 지라이슈_벌크_추가_요청값;
	}


	private void 지라이슈_요구사항_연결_끊고_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){
		지라이슈_엔티티_저장_목록.엔티티_목록_추가(this.지라이슈_삭제_적용(지라이슈_엔티티값));
	}

	private void 지라이슈_일괄_삭제_적용(){

		List<지라이슈_엔티티> 지라이슈_엔티티_요구사항_하위이슈_목록 = getBean(서브테스크_조회.class).요구사항_서브테스크_검색하기(지라이슈_벌크_추가_요청값);
		지라이슈_엔티티_요구사항_하위이슈_목록.forEach(
			this::지라이슈_삭제_적용
		);
		지라이슈_엔티티_저장_목록.엔티티_목록_리스트_전체_추가(지라이슈_엔티티_요구사항_하위이슈_목록);
	}

	@Override
	public void 수집() {
		this.지라이슈_요구사항_연결_끊고_삭제_적용(
			getBean(지라이슈_서비스_프로세스.class).이슈_조회하기(지라이슈_벌크_추가_요청값.조회조건_아이디())
		);
		// 하위이슈의 경우 요구사항 이슈가 삭제되었을 때 전부 삭제된다(모든 도큐먼트에 삭제 flag 추가)
		this.지라이슈_일괄_삭제_적용();
	}
}
