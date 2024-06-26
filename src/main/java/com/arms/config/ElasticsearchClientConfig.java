/**
 *
 */
package com.arms.config;

import com.arms.egovframework.javaservice.esframework.repository.공통저장소_구현체;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElementIterator;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
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

	private RestHighLevelClient client;

	@Override
	@Bean
	@SuppressWarnings("deprecation")
	public RestHighLevelClient elasticsearchClient() {

		final ClientConfiguration clientConfiguration =
				ClientConfiguration
				.builder()
				.connectedTo(this.elasticsearchUrl)
						.withHttpClientConfigurer(clientBuilder-> clientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy()))
				.withConnectTimeout(30000)
				.withSocketTimeout(30000)
				.build();

		this.client = RestClients.create(clientConfiguration).rest();
		return this.client;
	}

	@PreDestroy
	public void closeClient() {
		try {
			if (client != null) {
				client.close();
			}
		} catch (Exception e) {
			log.error("Error closing Elasticsearch client: ", e);
		}
	}

	@Bean(name = "keepAliveStrategy")
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (response, context) -> {
			HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			return 180*1000;
		};
	}
}
