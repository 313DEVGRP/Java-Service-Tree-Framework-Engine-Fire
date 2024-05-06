package com.arms.api.alm.analyis.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
class 요구사항_분석_서비스_프로세스Test {


	@Autowired
	요구사항_분석_서비스_프로세스 요구사항_분석_서비스_프로세스;

	@Test
	public void test(){

		/*Long pdServiceLink, Long[] pdServiceVersionLinks, LocalDate monthAgo;*/
		// 요구사항_분석_서비스_프로세스.누적데이터조회()
	}
}
