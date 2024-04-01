package com.arms.config;

import com.arms.elasticsearch.annotation.ElasticSearchIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class EsIndexTemplateConfig {

	private final ElasticsearchOperations operations;

	public EsIndexTemplateConfig(ElasticsearchOperations operations) {
		this.operations = operations;
	}

	@PostConstruct
	public void run() {

		Set<String> annotatedClasses
			= this.findAnnotatedClasses(ElasticSearchIndex.class, "com.arms.*");
		annotatedClasses.stream().map(clazz -> {
				try {
					return Class.forName(clazz);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException(e);
				}
			}).forEach(clazz->{
				Document document = AnnotationUtils.findAnnotation(clazz, Document.class);

				if(document!=null){

					var templateName = document.indexName()+"-template";
					var templatePattern = document.indexName()+"-*";
					var indexOperations = operations.indexOps(clazz);

					if (!indexOperations.existsTemplate(templateName)) {
						log.info("template-{} 생성진행",templateName);
						var mapping = indexOperations.createMapping();

						var aliasActions = new AliasActions().add(
							new AliasAction.Add(AliasActionParameters.builderForTemplate()
								.withAliases(indexOperations.getIndexCoordinates().getIndexNames())
								.build())
						);

						var request = PutTemplateRequest.builder(templateName, templatePattern)
							.withMappings(mapping)
							.withAliasActions(aliasActions)
							.build();

						indexOperations.putTemplate(request);
					}
				}
			});

	}

	public Set<String> findAnnotatedClasses(Class<? extends Annotation> annotationType, String... packagesToBeScanned)
	{
		var provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new
			AnnotationTypeFilter(annotationType));

		Set<String> ret = new HashSet<>();

		for (var pkg : packagesToBeScanned) {
			Set<BeanDefinition> beanDefs = provider.findCandidateComponents(pkg);
			beanDefs.stream()
				.map(BeanDefinition::getBeanClassName)
				.forEach(ret::add);
		}

		return ret;
	}

}
