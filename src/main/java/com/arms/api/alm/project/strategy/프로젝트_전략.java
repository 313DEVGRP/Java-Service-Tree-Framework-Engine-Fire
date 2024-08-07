package com.arms.api.alm.project.strategy;

import com.arms.api.alm.project.model.프로젝트_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface 프로젝트_전략 {

    프로젝트_데이터 프로젝트_상세정보_가져오기(서버정보_데이터 서버정보, String 프로젝트_키_또는_아이디) throws Exception;

    List<프로젝트_데이터> 프로젝트_목록_가져오기(서버정보_데이터 서버정보) throws URISyntaxException, IOException;

}
