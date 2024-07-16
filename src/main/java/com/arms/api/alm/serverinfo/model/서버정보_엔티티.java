package com.arms.api.alm.serverinfo.model;

import com.arms.api.util.common.constrant.index.인덱스자료;
import com.arms.egovframework.javaservice.esframework.annotation.ElasticSearchIndex;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = 인덱스자료.서버정보_인덱스명)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_class")
@JsonTypeName("com.arms.api.alm.serverinfo.model.서버정보_엔티티")
// @JsonTypeName("com.arms.api.index_entity.서버정보_인덱스")
@ElasticSearchIndex
@JsonIgnoreProperties(ignoreUnknown = true)
public class 서버정보_엔티티 {

    @Id
    @Field(type = FieldType.Keyword, name = "connectId")
    private String connectId;

    @Field(type = FieldType.Text, name = "type")
    private String type;

    @Field(type = FieldType.Text, name = "userId")
    private String userId;

    @Field(type = FieldType.Text, name = "passwordOrToken")
    private String passwordOrToken;

    @Field(type = FieldType.Text, name = "uri")
    private String uri;

    public 서버정보_엔티티() {
    }
}
