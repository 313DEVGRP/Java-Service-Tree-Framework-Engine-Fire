package com.arms.api.index_entity;


import com.arms.api.engine.common.constrant.index.인덱스자료;
import com.arms.elasticsearch.annotation.ElasticSearchIndex;
import com.arms.elasticsearch.annotation.RollingIndexName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = 인덱스자료.플루언트디_인덱스명, createIndex = false) // 인덱스명 _index 를 인덱스자료.플루언트디_인덱스명으로 가져가겠다.
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_class")
@JsonTypeName("com.arms.api.engine.models.Fluentd")
// @JsonTypeName("com.arms.api.index_entity.플루언트디_인덱스")
@JsonIgnoreProperties(ignoreUnknown = true)
@ElasticSearchIndex
public class 플루언트디_인덱스 {

    @Id
    @Field(type = FieldType.Keyword)
    private String id; // _id

    @Field(type = FieldType.Keyword, name= "@log_name")
    private String logName;

    @Field(type = FieldType.Date, name = "@timestamp", format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSZZZZZ")
    private Date timestamp;

    @Field(type = FieldType.Text, name ="log")
    private String log;

    @Field(type = FieldType.Text, name ="chunk")
    private String chunk;

    @Field(type = FieldType.Text, name ="chunk_id")
    private String chunk_id;

    @Field(type = FieldType.Keyword, name="container_id")
    private String container_id;

    @Field(type = FieldType.Keyword, name="container_name")
    private String container_name;

    @Field(type = FieldType.Text, name="error")
    private String error;

    @Field(type = FieldType.Text, name="message")
    private String message;

    @Field(type = FieldType.Text, name="partial_id")
    private String partial_id;

    @Field(type = FieldType.Text, name="partial_last")
    private String partial_last;

    @Field(type = FieldType.Text, name="partial_message")
    private String partial_message;

    @Field(type = FieldType.Text, name="partial_ordinal")
    private String partial_ordinal;

    @Field(type = FieldType.Text, name="retry_time")
    private String retry_time;

    @Field(type = FieldType.Keyword, name="source")
    private String source;

    @RollingIndexName
    private String indexName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 포맷 적용
        return LocalDate.now().format(formatter);
    }


}
