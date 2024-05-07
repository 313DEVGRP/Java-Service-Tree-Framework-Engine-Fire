/**
 *
 */
package com.arms.config;

import com.arms.elasticsearch.repository.공통저장소_구현체;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * @author Pratik Das
 *
 */
@Slf4j
@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.arms"},repositoryBaseClass = 공통저장소_구현체.class)
@ComponentScan(basePackages = { "com.arms"})
public class ElasticsearchClientConfig extends AbstractElasticsearchConfiguration {

	@Value("${elasticsearch.url}")
	public String elasticsearchUrl;

	private RestHighLevelClient restHighLevelClient;

	@Override
	@Bean
	@SuppressWarnings("deprecation")
	public RestHighLevelClient elasticsearchClient() {

		final ClientConfiguration clientConfiguration =
				ClientConfiguration
				.builder()
				.connectedTo(this.elasticsearchUrl)
				.withConnectTimeout(30000)
				.withSocketTimeout(30000)
				.build();

		this.restHighLevelClient = RestClients
									.create(clientConfiguration)
									.rest();

		return this.restHighLevelClient;
	}

	@PreDestroy
	public void closeClient() {
		if (this.restHighLevelClient != null) {
			try {
				this.restHighLevelClient.close();
			}
			catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}
}
