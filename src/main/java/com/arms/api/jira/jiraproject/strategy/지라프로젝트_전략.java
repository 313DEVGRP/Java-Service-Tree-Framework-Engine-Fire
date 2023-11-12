package com.arms.api.jira.jiraproject.strategy;

import com.arms.api.jira.jiraproject.model.지라프로젝트_데이터;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface 지라프로젝트_전략 {

    지라프로젝트_데이터 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception;

    List<지라프로젝트_데이터> 프로젝트_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException;

}
