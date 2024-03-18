package com.arms.api.engine.model;

import com.arms.api.engine.repository.인덱스자료;
import com.arms.elasticsearch.annotation.ElasticSearchIndex;
import com.arms.elasticsearch.annotation.Recent;
import com.arms.elasticsearch.annotation.RollingIndexName;
import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = 인덱스자료.지라이슈_인덱스명, createIndex = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_class")
@JsonTypeName("com.arms.api.engine.models.지라이슈")
@JsonIgnoreProperties(ignoreUnknown = true)
@ElasticSearchIndex
@EqualsAndHashCode(exclude = {"recent","timestamp"})
public class 지라이슈 {

    //////////////
    @Id
    @Field(type = FieldType.Keyword)
    private String id; // Elasticsearch의 문서 식별자

    @Recent
    @Field(type = FieldType.Boolean, name = "recent")
    private boolean recent;


    public 지라이슈() {
    }

    //////////////
    public void generateId() {
        if (timestamp == null) {
            this.timestamp = new Date();
        }
        this.id = this.jira_server_id + "_" + this.project.getKey() + "_" + this.key;
    }

    private Long jira_server_id;

    @Field(type = FieldType.Date, name = "@timestamp")
    private Date timestamp;

    @Field(type = FieldType.Auto, name = "issueID")
    private String issueID;

    @Field(type = FieldType.Keyword, name = "key")
    private String key;

    @Field(type = FieldType.Text, name = "self")
    private String self;

    @Field(type = FieldType.Keyword, name = "parentReqKey")
    private String parentReqKey;

    @Field(type = FieldType.Boolean, name = "isReq")
    private Boolean isReq;

    @Field(type = FieldType.Auto, name = "etc")
    private Object etc;

    @Field(type = FieldType.Percolator, name = "queries")
    private List<String> percolatorQueries;

//    @Field(type = FieldType.Nested)
    private 지라이슈.프로젝트 project;

//    @Field(type = FieldType.Nested)
    private 지라이슈.이슈유형 issuetype;

//    @Field(type = FieldType.Nested)
    private 지라이슈.생성자 creator;

//    @Field(type = FieldType.Nested)
    private 지라이슈.보고자 reporter;

//    @Field(type = FieldType.Nested, fielddata = true)
    private 지라이슈.담당자 assignee;

    @Field(type = FieldType.Text)
    private List<String> labels;

//    @Field(type = FieldType.Nested)
    private 지라이슈.우선순위 priority;

//    @Field(type = FieldType.Nested, fielddata = true)
    private 지라이슈.상태 status;

//    @Field(type = FieldType.Nested)
    private 지라이슈.해결책 resolution;

    @Field(type = FieldType.Text, name = "resolutiondate")
    private String resolutiondate;

    @Field(type = FieldType.Date, name = "created")
    private String created;

    @Field(type = FieldType.Date, name = "updated")
    private String updated;

//    @Field(type = FieldType.Nested)
    private List<지라이슈.워크로그> worklogs;

    @Field(type = FieldType.Integer, name = "timespent")
    private Integer timespent;

    @Field(type = FieldType.Text, name = "summary")
    private String summary;

    @Field(type = FieldType.Text, name = "pdServiceId", fielddata = true)
    private Long pdServiceId;

    @Field(type = FieldType.Long, name = "cReqLink")
    private Long cReqLink ;

    @Field(type = FieldType.Long, name = "pdServiceVersions")
    private Long[] pdServiceVersions;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 프로젝트 {
        @Field(type = FieldType.Text, name = "project_self")
        @JsonProperty("project_self")
        private String self;

        @Field(type = FieldType.Text, name = "project_id")
        @JsonProperty("project_id")
        private String id;

        @Field(type = FieldType.Text, name = "project_key")
        @JsonProperty("project_key")
        private String key;

        @Field(type = FieldType.Text, name = "project_name")
        @JsonProperty("project_name")
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 이슈유형 {
        // 온프레미스, 클라우드 공통
        @Field(type = FieldType.Text, name = "issuetype_self")
        @JsonProperty("issuetype_self")
        private String self;

        @Field(type = FieldType.Text, name = "issuetype_id")
        @JsonProperty("issuetype_id")
        private String id;

        @Field(type = FieldType.Text, name = "issuetype_description")
        @JsonProperty("issuetype_description")
        private String description;

        @Field(type = FieldType.Text, name = "issuetype_name")
        @JsonProperty("issuetype_name")
        private String name;

        @Field(type = FieldType.Boolean, name = "issuetype_subtask")
        @JsonProperty("issuetype_subtask")
        private Boolean subtask;

        // 클라우드만 사용
        @Field(type = FieldType.Text, name = "issuetype_untranslatedName")
        @JsonProperty("issuetype_untranslatedName")
        private String untranslatedName;

        @Field(type = FieldType.Integer, name = "issuetype_hierarchyLevel")
        @JsonProperty("issuetype_hierarchyLevel")
        private Integer hierarchyLevel;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 생성자 {

        @Field(type = FieldType.Text, name = "creator_accountId")
        @JsonProperty("creator_accountId")
        private String accountId;

        @Field(type = FieldType.Text, name = "creator_emailAddress")
        @JsonProperty("creator_emailAddress")
        private String emailAddress;

        @Field(type = FieldType.Text, name = "creator_displayName")
        @JsonProperty("creator_displayName")
        private String displayName;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 보고자 {

        @Field(type = FieldType.Text, name = "reporter_accountId")
        @JsonProperty("reporter_accountId")
        private String accountId;

        @Field(type = FieldType.Text, name = "reporter_emailAddress")
        @JsonProperty("reporter_emailAddress")
        private String emailAddress;

        @Field(type = FieldType.Text, name = "reporter_displayName")
        @JsonProperty("reporter_displayName")
        private String displayName;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 담당자 {

        @Field(type = FieldType.Text, name = "assignee_accountId")
        @JsonProperty("assignee_accountId")
        private String accountId;

        @Field(type = FieldType.Text, name = "assignee_emailAddress", fielddata = true)
        @JsonProperty("assignee_emailAddress")
        private String emailAddress;

        @Field(type = FieldType.Text, name = "assignee_displayName")
        @JsonProperty("assignee_displayName")
        private String displayName;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 우선순위 {

        // 온프레미스, 클라우드 공통
        @Field(type = FieldType.Text, name = "priority_self")
        @JsonProperty("priority_self")
        private String self;

        @Field(type = FieldType.Text, name = "priority_id")
        @JsonProperty("priority_id")
        private String id;

        @Field(type = FieldType.Text, name = "priority_name")
        @JsonProperty("priority_name")
        private String name;

        @Field(type = FieldType.Text, name = "priority_description")
        @JsonProperty("priority_description")
        private String description;

        // 클라우드
        @Field(type = FieldType.Boolean, name = "priority_isDefault")
        @JsonProperty("priority_isDefault")
        private boolean isDefault;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 상태 {

        // 온프레미스, 클라우드 공통
        @Field(type = FieldType.Text, name = "status_self")
        @JsonProperty("status_self")
        private String self;

        @Field(type = FieldType.Text, name = "status_id")
        @JsonProperty("status_id")
        private String id;

        @Field(type = FieldType.Text, name = "status_name", fielddata = true)
        @JsonProperty("status_name")
        private String name;

        @Field(type = FieldType.Text, name = "status_description")
        @JsonProperty("status_description")
        private String description;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 해결책 {


        // 온프레미스, 클라우드 공통
        @Field(type = FieldType.Text, name = "resolution_self")
        @JsonProperty("resolution_self")
        private String self;

        @Field(type = FieldType.Text, name = "resolution_id")
        @JsonProperty("resolution_id")
        private String id;

        @Field(type = FieldType.Text, name = "resolution_name")
        @JsonProperty("resolution_name")
        private String name;

        @Field(type = FieldType.Text, name = "resolution_description")
        @JsonProperty("resolution_description")
        private String description;

        // 클라우드
        @Field(type = FieldType.Boolean, name = "resolution_isDefault")
        @JsonProperty("resolution_isDefault")
        private boolean isDefault;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 워크로그 {

        @Field(type = FieldType.Text, name = "worklogs_self")
        @JsonProperty("worklogs_self")
        private String self;

        @Field(type = FieldType.Nested)
        private 저자 author;

        @Field(type = FieldType.Nested)
        private 수정한_저자 updateAuthor;

        @Field(type = FieldType.Text, name = "worklogs_created")
        @JsonProperty("worklogs_created")
        private String created;

        @Field(type = FieldType.Text, name = "worklogs_updated")
        @JsonProperty("worklogs_updated")
        private String updated;

        @Field(type = FieldType.Text, name = "worklogs_started")
        @JsonProperty("worklogs_started")
        private String started;

        @Field(type = FieldType.Text, name = "worklogs_timeSpent")
        @JsonProperty("worklogs_timeSpent")
        private String timeSpent;

        @Field(type = FieldType.Integer, name = "worklogs_timeSpentSeconds")
        @JsonProperty("worklogs_timeSpentSeconds")
        private Integer timeSpentSeconds;

        @Field(type = FieldType.Text, name = "worklogs_id")
        @JsonProperty("worklogs_id")
        private String id;

        @Field(type = FieldType.Text, name = "worklogs_issueId")
        @JsonProperty("worklogs_issueId")
        private String issueId;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 저자 {

        @Field(type = FieldType.Text, name = "worklogs_author_accountId")
        @JsonProperty("worklogs_author_accountId")
        private String accountId;

        @Field(type = FieldType.Text, name = "worklogs_author_emailAddress")
        @JsonProperty("worklogs_author_emailAddress")
        private String emailAddress;

    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode
    public static class 수정한_저자 {

        @Field(type = FieldType.Text, name = "worklogs_updateAuthor_accountId")
        @JsonProperty("worklogs_updateAuthor_accountId")
        private String accountId;

        @Field(type = FieldType.Text, name = "worklogs_updateAuthor_emailAddress")
        @JsonProperty("worklogs_updateAuthor_emailAddress")
        private String emailAddress;

    }

    @RollingIndexName
    private String localDate() {
        return String.valueOf(LocalDate.now());
    }
}
