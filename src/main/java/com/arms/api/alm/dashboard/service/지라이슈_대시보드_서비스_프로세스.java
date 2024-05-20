package com.arms.api.alm.dashboard.service;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.api.util.model.dto.제품버전목록;
import com.arms.api.util.model.dto.트리맵_검색__집계_하위_요청;
import com.arms.api.util.model.vo.TaskList;
import com.arms.api.util.model.vo.Worker;
import com.arms.elasticsearch.query.factory.creator.쿼리_생성기;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("지라이슈_대시보드_서비스")
@AllArgsConstructor
public class 지라이슈_대시보드_서비스_프로세스 implements 지라이슈_대시보드_서비스 {

    private 지라이슈_저장소 지라이슈_저장소;

    @Override
    public 버킷_집계_결과_목록_합계 집계결과_가져오기(쿼리_생성기 쿼리_생성기) {

        return 지라이슈_저장소.버킷집계(
                쿼리_생성기.생성()
        );
    }

    @Override
    public List<Worker> 작업자_별_요구사항_별_관여도(트리맵_검색__집계_하위_요청 트리맵_집계_요청) {
        Map<String, Worker> contributionMap = new HashMap<>();

        List<제품버전목록> 제품버전목록데이터 = 트리맵_집계_요청.get제품버전목록();
       
        List<지라이슈_엔티티> requirementIssues = 지라이슈_저장소.findByIsReqAndPdServiceIdAndPdServiceVersionsIn(true, 트리맵_집계_요청.getPdServiceLink(), 트리맵_집계_요청.getPdServiceVersionLinks());

        // 요구사항의 키를 모두 추출
        List<String> allReqKeys = requirementIssues.stream().map(지라이슈_엔티티::getKey).collect(Collectors.toList());

        // 모든 하위 태스크를 한 번에 로드
        List<지라이슈_엔티티> allSubTasks = 지라이슈_저장소.findByParentReqKeyIn(allReqKeys);

        // 하위 태스크를 부모 키로 그룹화
        Map<String, List<지라이슈_엔티티>> subTasksByParent = allSubTasks.stream()
                .filter(subtask -> subtask.getAssignee() != null)
                .collect(Collectors.groupingBy(지라이슈_엔티티::getParentReqKey));

        requirementIssues.stream().forEach(reqIssue -> {
            String key = reqIssue.getKey();
            String summary = reqIssue.getSummary() == null ? "해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다." : reqIssue.getSummary();
            Long[] pdServiceVersions = reqIssue.getPdServiceVersions();
            String versionNames =  Stream.of(pdServiceVersions)
                    .map(versionId -> 제품버전목록데이터.stream()
                            .filter(p -> p.getC_id().equals(versionId.toString()))
                            .findFirst()
                            .map(제품버전목록::getC_title)
                            .orElse(null)
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));

            Optional.ofNullable(subTasksByParent.get(key)).orElse(Collections.emptyList()).stream().forEach(subtask -> {
                String assigneeId = subtask.getAssignee().getAccountId();
                String displayName = subtask.getAssignee().getDisplayName();

                Worker worker = contributionMap.computeIfAbsent(assigneeId, id -> {
                    Map<String, Integer> dataMap = new HashMap<>();
                    dataMap.put("totalInvolvedCount", 0);
                    return new Worker(assigneeId, displayName, dataMap, new ArrayList<>());
                });

                TaskList taskList = worker.getChildren().stream()
                        .filter(task -> task.getId().equals(key))
                        .findFirst()
                        .orElseGet(() -> {
                            Map<String, Integer> dataList = new HashMap<>();
                            dataList.put("involvedCount", 0);
                            TaskList newTask = new TaskList(key, "[ " + versionNames + " ] - " + summary, dataList);
                            worker.getChildren().add(newTask);
                            return newTask;
                        });

                taskList.getData().put("involvedCount", taskList.getData().get("involvedCount") + 1);
                worker.getData().put("totalInvolvedCount", worker.getData().get("totalInvolvedCount") + 1);
            });
        });

        return contributionMap.values().stream()
                .sorted((w1, w2) -> w2.getData().get("totalInvolvedCount").compareTo(w1.getData().get("totalInvolvedCount")))
                .limit(트리맵_집계_요청.get크기() > 0 ? 트리맵_집계_요청.get크기() : Long.MAX_VALUE)
                .collect(Collectors.toList());

    }



}
