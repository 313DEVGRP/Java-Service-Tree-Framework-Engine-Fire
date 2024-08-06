package com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync.abastract.ALM_수집_데이터_지라이슈_동기화_추상클래스;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_streategy.이슈전략_호출;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.arms.config.ApplicationContextProvider.getBean;

@Slf4j
public class ALM_수집_데이터_증분_지라이슈_엔티티_저장_처리 extends ALM_수집_데이터_지라이슈_동기화_추상클래스 {

	private final 지라이슈_엔티티_컬렉션 지라이슈_엔티티_저장_목록 = new 지라이슈_엔티티_컬렉션(new ArrayList<>());

	private final 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값;

	@Override
	public List<지라이슈_엔티티> 지라이슈_앤티티_저장할_목록_가져오기(){
		return 지라이슈_엔티티_저장_목록.get지라이슈_엔티티_목록();
	}

	@Override
	public void 수집() {
		this.지라이슈_데이터에_존재하지_않는_지라이슈_엔티티_삭제_적용();

		this.지라이슈_엔티티_하위이슈_목록_적용(getBean(이슈전략_호출.class).증분서브테스크_가져오기(지라이슈_벌크_추가_요청값));

		this.지라이슈_엔티티_연결이슈_적용();
	}

	public ALM_수집_데이터_증분_지라이슈_엔티티_저장_처리(
		 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값
	){
        super(
				지라이슈_벌크_추가_요청값
				,getBean(이슈전략_호출.class).증분이슈링크_가져오기(지라이슈_벌크_추가_요청값)
		);
        this.지라이슈_벌크_추가_요청값 = 지라이슈_벌크_추가_요청값;
	}

}
