package com.arms.api.jira.jiraissue.strategy;

import com.arms.api.jira.jiraissue.model.지라이슈생성_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈_데이터;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface 지라이슈_전략 {

    List<지라이슈_데이터> 이슈_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception;

    지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디)throws Exception;

    지라이슈_데이터 이슈_생성하기(Long 연결_아이디,
                            지라이슈생성_데이터 지라이슈생성_데이터) throws Exception;

    Map<String,Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디,
                                    지라이슈생성_데이터 지라이슈생성_데이터) throws Exception;

    Map<String,Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception;

    List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException;

    List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException;

}
