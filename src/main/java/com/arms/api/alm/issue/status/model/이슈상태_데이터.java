package com.arms.api.alm.issue.status.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 이슈상태_데이터 {

    private String self;

    private String id;

    private String name;

    private String description;

    private String issueTypeId;

    public 이슈상태_데이터(String self, String id, String name) {
        this.self = self;
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(Long.parseLong(id));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        이슈상태_데이터 that = (이슈상태_데이터) obj;
        return self.equals(that.self);
    }
}
