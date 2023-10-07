package com.arms.elasticsearch.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class 지라이슈_검색엔진Test {

	@Autowired
	private 지라이슈_서비스 지라이슈_서비스;

	@Test
	public void test() throws IOException {
		지라이슈_서비스.특정필드의_값들을_그룹화하여_빈도수가져오기("pdServiceVersion");
	}
}
