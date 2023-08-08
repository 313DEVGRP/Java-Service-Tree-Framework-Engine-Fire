package com.arms.jira.cloud.jiraissuetype.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudJiraIssueTypeDTO {
	private String self;
	private String id;
	private String description;

	// non use
	//private String iconUrl;
	private String name;
	private String untranslatedName;
	private Boolean subtask;

	// non use
	//private Integer avatarId;
	private Integer hierarchyLevel;
}
