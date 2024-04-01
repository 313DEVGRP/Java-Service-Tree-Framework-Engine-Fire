/**
 *
 */
package com.arms.config;

import com.arms.elasticsearch.repository.공통저장소_구현체;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author Pratik Das
 *
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.arms.api.alm.issue.repository", "com.arms.api.alm.fluentd.repository", "com.arms.api.alm.serverinfo.repository"},repositoryBaseClass = 공통저장소_구현체.class)
@ComponentScan(basePackages = { "com.arms.elasticsearch" ,"com.arms.api.alm.serverinfo"})
public class ElasticsearchClientConfig extends AbstractElasticsearchConfiguration {

	@Value("${elasticsearch.url}")
	public String elasticsearchUrl;

	@Override
	@Bean
	@SuppressWarnings("deprecation")
	public RestHighLevelClient elasticsearchClient() {

		final ClientConfiguration clientConfiguration =
				ClientConfiguration
				.builder()
				.connectedTo(elasticsearchUrl)
				.withConnectTimeout(30000)
				.withSocketTimeout(30000)
				.build();

		return RestClients
				.create(clientConfiguration)
				.rest();
	}


}
