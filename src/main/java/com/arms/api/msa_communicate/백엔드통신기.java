package com.arms.api.msa_communicate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "backend-core", url = "${arms.backend-core.url}")
public interface 백엔드통신기 {

    @GetMapping("/arms/jiraServerPure/getJiraServerMonitor.do")
    ResponseEntity<?> 지라서버정보_가져오기();

}
