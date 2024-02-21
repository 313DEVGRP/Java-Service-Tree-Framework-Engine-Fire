package com.arms.api.engine.dtos;

import com.arms.api.engine.models.지라이슈;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 요구사항_지라이슈키별_업데이트_목록_데이터 {
    String key;
    String parentReqKey;
    String updated;
    String resolutiondate;
    private 지라이슈.담당자 assignee;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        요구사항_지라이슈키별_업데이트_목록_데이터 that = (요구사항_지라이슈키별_업데이트_목록_데이터) o;
        return Objects.equals(key, that.key)
                && Objects.equals(updated, that.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, updated);
    }
}
