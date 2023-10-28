package com.arms.elasticsearch.services.dashboard.treemap;

import com.arms.elasticsearch.models.dashboard.treemap.TaskList;
import com.arms.elasticsearch.models.dashboard.treemap.Worker;
import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("지라이슈_대시보드_작업자별_요구사항_관여")
@RequiredArgsConstructor
public class TreeMapChartImpl implements TreeMapChart {

    private final 지라이슈_저장소 지라이슈저장소;

    @Override
    public List<Worker> 작업자_별_요구사항_별_관여도(Long pdServiceLink, List<Long> pdServiceVersionLinks) {
        Map<String, Worker> contributionMap = new HashMap<>();

        List<지라이슈> requirementIssues = 지라이슈저장소.findByIsReqAndPdServiceIdAndPdServiceVersionIn(true, pdServiceLink, pdServiceVersionLinks);

        // 요구사항의 키를 모두 추출
        List<String> allReqKeys = requirementIssues.stream().map(지라이슈::getKey).collect(Collectors.toList());

        // 모든 하위 태스크를 한 번에 로드
        List<지라이슈> allSubTasks = 지라이슈저장소.findByParentReqKeyIn(allReqKeys);

        // 하위 태스크를 부모 키로 그룹화
        Map<String, List<지라이슈>> subTasksByParent = allSubTasks.stream()
                .filter(subtask -> subtask.getAssignee() != null)
                .collect(Collectors.groupingBy(지라이슈::getParentReqKey));

        for (지라이슈 reqIssue : requirementIssues) {
            String key = reqIssue.getKey();
            String summary = reqIssue.getSummary();

            List<지라이슈> subTasks = subTasksByParent.getOrDefault(key, Collections.emptyList());

            for (지라이슈 subtask : subTasks) {
                String assigneeId = subtask.getAssignee().getAccountId();
                String displayName = subtask.getAssignee().getDisplayName();

                Worker worker = contributionMap.computeIfAbsent(
                        assigneeId,
                        k -> new Worker(assigneeId, displayName)
                );

                worker.getChildren().stream()
                        .filter(taskList -> taskList.getId().equals(key))
                        .findFirst()
                        .ifPresentOrElse(
                                existingTaskList -> existingTaskList.setInvolvedCount(existingTaskList.getInvolvedCount() + 1),
                                () -> worker.getChildren().add(new TaskList(key, summary, 1))
                        );

                worker.setTotalInvolvedCount(worker.getTotalInvolvedCount() + 1);
            }
        }

        return contributionMap.values().stream()
                .sorted(Comparator.comparingInt(Worker::getTotalInvolvedCount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

}
