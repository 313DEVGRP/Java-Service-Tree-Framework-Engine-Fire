package com.arms.api.alm.issuetype.strategy;

import com.arms.api.alm.issuetype.model.이슈유형_데이터;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface 이슈유형_전략 {

    List<이슈유형_데이터> 이슈유형_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException;

    List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException;
}
