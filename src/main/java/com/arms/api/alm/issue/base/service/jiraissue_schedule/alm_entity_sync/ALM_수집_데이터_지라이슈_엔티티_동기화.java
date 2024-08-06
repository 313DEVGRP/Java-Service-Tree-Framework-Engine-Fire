package com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync;

import static com.arms.config.ApplicationContextProvider.*;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync.abastract.ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스;

import java.util.List;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_streategy.이슈전략_호출;

public class ALM_수집_데이터_지라이슈_엔티티_동기화 {

	private final 지라이슈_데이터 지라이슈_데이터;

	private final 요구사항이슈_처리 요구사항이슈_처리;

	private final ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스 ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스;

	public ALM_수집_데이터_지라이슈_엔티티_동기화(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {

		this.지라이슈_데이터 = getBean(이슈전략_호출.class).이슈_상세정보_가져오기(지라이슈_벌크_추가_요청값);

		this.요구사항이슈_처리 = new 요구사항이슈_처리(지라이슈_데이터,지라이슈_벌크_추가_요청값);

		if(가져온_ALM_이슈_없음()){
			this.ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스 = new ALM_수집_데이터_공통_지라이슈_엔티티_삭제_처리(지라이슈_벌크_추가_요청값);
		}else{
			this.ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스 = new ALM_수집_데이터_지라이슈_엔티티_저장_처리(
				지라이슈_벌크_추가_요청값
			);
		}
		this.ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스.수집();

	}

	public List<지라이슈_엔티티> 지라이슈_앤티티_저장할_목록_가져오기(){
		if(가져온_ALM_이슈_없음()){
			return ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스.지라이슈_앤티티_저장할_목록_가져오기();
		}else{
			지라이슈_엔티티_컬렉션 지라이슈_엔티티_컬렉션 = new 지라이슈_엔티티_컬렉션(ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스.지라이슈_앤티티_저장할_목록_가져오기());
			지라이슈_엔티티_컬렉션.엔티티_목록_추가(this.요구사항이슈_처리.요구사항_이슈());
			return 지라이슈_엔티티_컬렉션.get지라이슈_엔티티_목록();
		}
	}

	private boolean 가져온_ALM_이슈_없음(){
		return 지라이슈_데이터 ==null;
	}



}
