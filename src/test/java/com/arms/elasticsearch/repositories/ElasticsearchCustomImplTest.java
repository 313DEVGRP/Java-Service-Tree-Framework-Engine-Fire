package com.arms.elasticsearch.repositories;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.test.context.ActiveProfiles;

import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.repositories.지라이슈_저장소;

@SpringBootTest
@ActiveProfiles("dev")
class ElasticsearchCustomImplTest {


	@Autowired
	지라이슈_저장소 저장소;

	@Test
	public void test() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		지라이슈 지라이슈 = new 지라이슈();
		// 지라이슈.setId("1000");
		// testHelper(지라이슈.class);

		try{
			com.arms.api.engine.models.지라이슈 byId = 저장소.getById("7634806241763211551_SP_SP-756", 지라이슈.class);
			System.out.println(byId);

			// testHelper(지라이슈);
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
		}

	}

	public <T> void testHelper(Class<T> valueType) throws ClassNotFoundException {

		System.out.println(valueType.getTypeName());
		Document annotation = Class.forName(valueType.getTypeName()).getAnnotation(Document.class);
		System.out.println(annotation.indexName());
	}

	public <T> void testHelper2(T t) {
		Field[] fields = t.getClass().getDeclaredFields();

		Arrays.stream(fields).flatMap(field ->
			Arrays.stream(field.getDeclaredAnnotations())
				.filter(annotation -> (annotation.annotationType()==Id.class))
				.map(annotation -> {
					try {
						field.setAccessible(true);
						if(field.get(t)!=null){
							return (String)field.get(t);
						}else{
							throw new RuntimeException("값이 없습니다.");
						}
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
		).findFirst().orElseThrow();

	}

}
