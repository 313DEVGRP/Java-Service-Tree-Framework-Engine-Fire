package com.arms.api.engine.services.dashboard.treemap;

import com.arms.api.engine.models.dashboard.treemap.TaskList;
import com.arms.api.engine.models.dashboard.treemap.Worker;
import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.repositories.지라이슈_저장소;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("지라이슈_대시보드_작업자별_요구사항_관여")
@RequiredArgsConstructor
public class TreeMapChartImpl implements TreeMapChart {

    public static final String FIELD_INVOLVED_COUNT = "involvedCount";
    public static final String FIELD_TOTAL_INVOLVED_COUNT = "totalInvolvedCount";
    private final 지라이슈_저장소 지라이슈저장소;

    @Override
    public List<Worker> 작업자_별_요구사항_별_관여도(Long pdServiceLink, List<Long> pdServiceVersionLinks, int maxResults) {
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

                Worker worker = contributionMap.computeIfAbsent(assigneeId, id -> {
                    Map<String, Integer> dataMap = new HashMap<>();
                    dataMap.put(FIELD_TOTAL_INVOLVED_COUNT, 0);
                    return new Worker(assigneeId, displayName, dataMap, new ArrayList<>());
                });


                TaskList taskList = worker.getChildren().stream()
                        .filter(task -> task.getId().equals(key))
                        .findFirst()
                        .orElseGet(() -> {
                            Map<String, Integer> dataList = new HashMap<>();
                            dataList.put(FIELD_INVOLVED_COUNT, 0);
                            TaskList newTask = new TaskList(key, summary, dataList);
                            worker.getChildren().add(newTask);
                            return newTask;
                        });

                taskList.getData().put(FIELD_INVOLVED_COUNT, taskList.getData().get(FIELD_INVOLVED_COUNT) + 1);
                worker.getData().put(FIELD_TOTAL_INVOLVED_COUNT, worker.getData().get(FIELD_TOTAL_INVOLVED_COUNT) + 1);
            }
        }


        if (maxResults > 0) {
            return contributionMap.values().stream()
                    .sorted((w1, w2) -> w2.getData().get(FIELD_TOTAL_INVOLVED_COUNT).compareTo(w1.getData().get(FIELD_TOTAL_INVOLVED_COUNT)))
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }

        return contributionMap.values().stream()
                .sorted((w1, w2) -> w2.getData().get(FIELD_TOTAL_INVOLVED_COUNT).compareTo(w1.getData().get(FIELD_TOTAL_INVOLVED_COUNT)))
                .collect(Collectors.toList());

    }

}
