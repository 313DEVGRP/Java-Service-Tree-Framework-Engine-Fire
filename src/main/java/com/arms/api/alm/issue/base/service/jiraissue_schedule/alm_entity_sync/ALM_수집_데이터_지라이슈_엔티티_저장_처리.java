package com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_streategy.이슈전략_호출;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.main.이슈_스케쥴_서비스_프로세스;
import com.arms.api.alm.issue.base.service.jiraissue_schedule.subtask_repository.서브테스크_조회;
import com.arms.api.alm.utils.지라이슈_생성;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.arms.config.ApplicationContextProvider.getBean;

@Slf4j
public class ALM_수집_데이터_지라이슈_엔티티_저장_처리 implements ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스{

	private final 지라이슈_엔티티_컬렉션 지라이슈_엔티티_저장_목록 = new 지라이슈_엔티티_컬렉션(new ArrayList<>());

	private final List<지라이슈_데이터> 지라이슈_데이터_하위이슈_목록;

	private final List<지라이슈_데이터> 지라이슈_데이터_연결이슈_목록;

	private final 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값;

	@Override
	public List<지라이슈_엔티티> 지라이슈_앤티티_저장할_목록_가져오기(){
		return 지라이슈_엔티티_저장_목록.get지라이슈_엔티티_목록();
	}

	@Override
	public void 수집() {
		this.지라이슈_데이터에_존재하지_않는_지라이슈_엔티티_삭제_적용();

		this.지라이슈_엔티티_하위이슈_목록_적용();

		this.지라이슈_엔티티_연결이슈_적용();
	}

	public ALM_수집_데이터_지라이슈_엔티티_저장_처리(
		 지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값
		,List<지라이슈_데이터> 지라이슈_데이터_하위이슈_목록
		,List<지라이슈_데이터> 지라이슈_데이터_연결이슈_목록

	){

		this.지라이슈_벌크_추가_요청값 = 지라이슈_벌크_추가_요청값;

		this.지라이슈_데이터_하위이슈_목록 = 지라이슈_데이터_하위이슈_목록;

		this.지라이슈_데이터_연결이슈_목록 = 지라이슈_데이터_연결이슈_목록;

	}



	private void 지라이슈_일괄_삭제_적용(List<지라이슈_엔티티> 요구사항_서브테스크_삭제_목록값){
		요구사항_서브테스크_삭제_목록값.forEach(
			this::지라이슈_삭제_적용
		);
		지라이슈_엔티티_저장_목록.엔티티_목록_리스트_전체_추가(요구사항_서브테스크_삭제_목록값);
	}

	private void 지라이슈_데이터에_존재하지_않는_지라이슈_엔티티_삭제_적용(){
		지라이슈_데이터_컬렉션 지라이슈_데이터_컬렉션 = new 지라이슈_데이터_컬렉션(getBean(이슈전략_호출.class).서브테스크_가져오기(지라이슈_벌크_추가_요청값));
		지라이슈_엔티티_컬렉션 지라이슈_엔티티_컬렉션 = new 지라이슈_엔티티_컬렉션(getBean(서브테스크_조회.class).요구사항_서브테스크_검색하기(지라이슈_벌크_추가_요청값));
		this.지라이슈_일괄_삭제_적용(
			지라이슈_데이터_컬렉션.지라이슈_데이터에_존재하지_않는_지라이슈_목록(지라이슈_엔티티_컬렉션)
				.get지라이슈_엔티티_목록()
		);
	}

	/**
	 * 스케줄러 작동 시 암스에서 생성한 요구사항 자체가 전날 업데이트가 일어났는지 확인 시 업데이트가 없을 시 null 반환(삭제된 이슈를 조회할 때 또한)
	 * 따라서 암스 생성 요구사항 상세정보를 JIRA에서 조회 후 어플리케이션 단에서 updated 항목을 검증 후 증분 데이터 판단 후 저장시키는 방법
	 **/

	private void 지라이슈_엔티티_하위이슈_목록_적용() {
		List<지라이슈_엔티티> 지라이슈_엔티티들 = this.지라이슈_데이터_하위이슈_목록
			.stream()
			.map(ALM서브테스크 -> {
				return 지라이슈_생성.ELK_데이터로_변환(
					지라이슈_벌크_추가_요청값.get지라서버_아이디()
					, ALM서브테스크
					, false
					, 지라이슈_벌크_추가_요청값.get이슈_키()
					, 지라이슈_벌크_추가_요청값.get제품서비스_아이디()
					, 지라이슈_벌크_추가_요청값.get제품서비스_버전들()
					, 지라이슈_벌크_추가_요청값.getCReqLink()
				);
			}).collect(Collectors.toList());

		지라이슈_엔티티_저장_목록.엔티티_목록_리스트_전체_추가(지라이슈_엔티티들);
	}

	private void 지라이슈_엔티티_연결이슈_적용(){

		this.지라이슈_데이터_연결이슈_목록
			.forEach(ALM연결이슈 -> {
				// 조회한 연결이슈가 요구사항 or ALM에서 생성하지 않은 이슈 or 다른 요구사항의 하위 이슈인 경우 고려해야 함
				String 조회_아이디 = 지라이슈_벌크_추가_요청값.조회조건_아이디(ALM연결이슈);

				지라이슈_엔티티 조회_결과 = getBean(이슈_스케쥴_서비스_프로세스.class).이슈_조회하기(조회_아이디);
				Optional<지라이슈_엔티티> 저장할_이슈_엔티티 = 지라이슈_엔티티_저장_목록.get지라이슈_엔티티_목록().stream()
						.filter(이슈 -> {
							String 비교_아이디 = 이슈.조회조건_아이디();
							return 비교_아이디.equals(조회_아이디);
						})
						.findFirst();

				if (조회_결과 == null && !저장할_이슈_엔티티.isPresent()) {
					지라이슈_엔티티 변환된_이슈 = 지라이슈_생성.ELK_데이터로_변환(
						지라이슈_벌크_추가_요청값.get지라서버_아이디()
						, ALM연결이슈
						, null // 요구사항 여부: 확인 불가하므로 null 설정
						, null // 부모 요구사항 키: 지정 하지 않음
						, null // 제품 서비스: 다른 제품 서비스 가능
						, null // 버전: 다른 버전의 요구사항 연결 가능
						, null // 요구사항 아이디: 다른 요구사항 연결 가능
					);

					지라이슈_엔티티_저장_목록.엔티티_목록_추가(변환된_이슈);
				}

			});
	}

}
