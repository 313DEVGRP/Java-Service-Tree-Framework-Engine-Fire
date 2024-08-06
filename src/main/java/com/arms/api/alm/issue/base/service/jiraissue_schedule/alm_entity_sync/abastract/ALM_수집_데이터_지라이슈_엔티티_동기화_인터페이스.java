package com.arms.api.alm.issue.base.service.jiraissue_schedule.alm_entity_sync.abastract;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;

public interface ALM_수집_데이터_지라이슈_엔티티_동기화_인터페이스 {

	List<지라이슈_엔티티> 지라이슈_앤티티_저장할_목록_가져오기();

	void 수집();

	default 지라이슈_엔티티 지라이슈_삭제_적용(지라이슈_엔티티 지라이슈_엔티티값){

		String 이슈_삭제_년월일 = LocalDate.now()
			.minusDays(1)
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		if(지라이슈_엔티티값!=null&&지라이슈_엔티티값.izNotEmpty()){
			지라이슈_엔티티.삭제 삭제데이터 = 지라이슈_엔티티값.getDeleted();
			if (삭제데이터 == null) {
				삭제데이터 = new 지라이슈_엔티티.삭제();
			}

			if (삭제데이터.getIsDeleted() == null || 삭제데이터.getIsDeleted()) {
				삭제데이터.setDeleted_date(이슈_삭제_년월일);
				삭제데이터.setIsDeleted(true);
				지라이슈_엔티티값.setDeleted(삭제데이터);
			}
		}

		return 지라이슈_엔티티값;

	}
}
