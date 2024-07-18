package com.arms.config;

import com.arms.egovframework.javaservice.esframework.repository.공통저장소;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.lang.reflect.Method;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public ApplicationListener<ContextRefreshedEvent> validateRepositories() {
        return event -> {
            EnableElasticsearchRepositories enableElasticsearchRepositories = ElasticsearchClientConfig.class.getAnnotation(EnableElasticsearchRepositories.class);
            String[] basePackages = enableElasticsearchRepositories.basePackages();

            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false){
                        @Override
                        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                            return super.isCandidateComponent(beanDefinition) || beanDefinition.getMetadata().isInterface();
                        }
                    };

            TypeFilter interfaceFilter = (metadataReader, metadataReaderFactory) -> {
                try {
                    // 클래스 이름 가져오기
                    String className = metadataReader.getClassMetadata().getClassName();
                    // 클래스 로드
                    Class<?> clazz = Class.forName(className);
                    return 공통저장소.class.isAssignableFrom(clazz) && clazz.isInterface() && !clazz.equals(공통저장소.class);
                } catch (Exception e) {
                    throw new RuntimeException("클래스 스캔 실패", e);
                }
            };

            scanner.addIncludeFilter(interfaceFilter);

            for (String basePackage : basePackages) {

                for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
                    try{

                        Class<?> beanClass = Class.forName(bd.getBeanClassName());

                        Method[] methods = beanClass.getDeclaredMethods();

                        if(methods.length>0){
                            throw new IllegalStateException("공통저장소를 상속한 인터페이스는 메소드를 가질수 없습니다. " + beanClass);
                        }

                    }catch (ClassNotFoundException e){
                        throw new RuntimeException("클래스가 없습니다.",e);
                    }
                }
            }
        };
    }

}