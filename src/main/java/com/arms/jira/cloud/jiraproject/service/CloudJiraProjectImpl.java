package com.arms.jira.cloud.jiraproject.service;


import com.arms.jira.utils.지라유틸;
import com.arms.jira.cloud.jiraproject.model.CloudJiraProjectDTO;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.service.지라연결_서비스;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@AllArgsConstructor
@Service
public class CloudJiraProjectImpl implements CloudJiraProject {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private 지라연결_서비스 지라연결_서비스;

	@Override
	public CloudJiraProjectDTO getProjectData(Long connectId, String projectKey) throws Exception {
		String endpoint = "/rest/api/3/project/"+ projectKey;

		지라연결정보_데이터 found = 지라연결_서비스.checkInfo(connectId);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraProjectDTO project = 지라유틸.get(webClient, endpoint, CloudJiraProjectDTO.class).block();

        logger.info(project.toString());

        return project;
	}

	@Override
	public List<CloudJiraProjectDTO> getProjectList(Long connectId) throws Exception {

		String endpoint = "/rest/api/3/project";

		지라연결정보_데이터 found = 지라연결_서비스.checkInfo(connectId);

		if (found == null) {
			// throw Exception e; ControllerAdvice 오류 처리
		}

		WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

		// ObjectMapper objectMapper = new ObjectMapper();
		// String response = 지라유틸.get(webClient, endpoint, String.class).block();
		// List<CloudJiraProjectDTO> projects = objectMapper.readValue(response, new TypeReference<List<CloudJiraProjectDTO>>() {});

		List<CloudJiraProjectDTO> projects = 지라유틸.get(webClient, endpoint, new ParameterizedTypeReference<List<CloudJiraProjectDTO>>() {}).block();

        logger.info(projects.toString());

	    return projects;
	}
}
