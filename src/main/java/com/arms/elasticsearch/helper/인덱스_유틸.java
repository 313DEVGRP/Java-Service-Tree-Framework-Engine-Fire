package com.arms.elasticsearch.helper;

import com.arms.api.engine.models.지라이슈;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.tasks.GetTaskRequest;
import org.elasticsearch.client.tasks.GetTaskResponse;
import org.elasticsearch.client.tasks.TaskSubmissionResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Component
public class 인덱스_유틸 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final ElasticsearchOperations 엘라스틱서치_작업;

    @Autowired
    private RestHighLevelClient client;

    public 인덱스_유틸(ElasticsearchOperations elasticsearchOperations) {
        this.엘라스틱서치_작업 = elasticsearchOperations;
    }

    public boolean 인덱스확인_및_생성_매핑(Class<?> clazz) {
        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(clazz);
        boolean 인덱스확인 = 인덱스확인(인덱스작업);

        if (인덱스확인) {
            return true;
        }

        boolean 생성확인 = 인덱스작업.create();
        if (!생성확인) {
            throw new IllegalStateException("인덱스 생성에 실패하였습니다.");
        }

        boolean 매핑확인 = 인덱스작업.putMapping(인덱스작업.createMapping());

        if (!매핑확인) {
            throw new IllegalStateException("인덱스 매핑 설정에 실패하였습니다.");
        }

        인덱스작업.refresh();
        로그.info("Created index: " + clazz.getSimpleName().toLowerCase());

        return 매핑확인;
    }

    public boolean 인덱스확인(IndexOperations 인덱스작업) {

        boolean 확인결과 = 인덱스작업.exists();

        return 확인결과;
    }

    public boolean 리인덱스(String 현재_지라이슈인덱스, String 백업_지라이슈인덱스) {
        if (!인덱스_존재_확인(백업_지라이슈인덱스)) {
            if (!인덱스_존재_확인(현재_지라이슈인덱스)) {
                로그.info("현재 인덱스 정보가 없습니다.");
                return true;
            }

            if (!인덱스_백업_생성(백업_지라이슈인덱스, 지라이슈.class)) {
                return false;
            }
        } else {
            로그.info("백업 인덱스 정보가 있습니다.");
            return true;
        }

        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices(현재_지라이슈인덱스);
        reindexRequest.setDestIndex(백업_지라이슈인덱스);
        reindexRequest.setSourceBatchSize(5000);

        boolean 리인덱스결과 = false;
        try {
            BulkByScrollResponse reindexResponse = client.reindex(reindexRequest, RequestOptions.DEFAULT);
            if(reindexResponse.getBulkFailures().size() == 0) {
                리인덱스결과 = true;
            }
        } catch (IOException e) {
            로그.error("Reindex Error : " + e.getMessage());
            throw new RuntimeException(e);
        }

        return 리인덱스결과;
    }

    public boolean 인덱스삭제(String 삭제할_지라이슈인덱스) {
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest(삭제할_지라이슈인덱스);
        boolean 삭제결과 = false;

        try {
            AcknowledgedResponse deleteResponse = client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
            if (deleteResponse.isAcknowledged()) {
                삭제결과 = true;
            }
        } catch (IOException e) {
            로그.error("Index Delete Error : " + e.getMessage());
            throw new RuntimeException(e);
        }

        return 삭제결과;
    }

    public boolean 인덱스삭제(Class<?> clazz) {
        boolean 삭제결과 = false;

        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(clazz);

        try {
            if (인덱스작업.exists()) {
                삭제결과 = 인덱스작업.delete();
            }
        }
        catch(Exception e) {
            로그.error(e.getMessage());
        }

        return 삭제결과;
    }

    public boolean 인덱스백업(Class<?> clazz) {
        String 현재_지라이슈인덱스 = 인덱스자료.지라이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "-" + currentDate;

        if (!인덱스_존재_확인(백업_지라이슈인덱스)) {
            if (!인덱스_존재_확인(현재_지라이슈인덱스)) {
                로그.info("현재 인덱스 정보가 없습니다.");
                return true;
            }

            if (!인덱스_백업_생성(백업_지라이슈인덱스, clazz)) {
                return false;
            }

            if(인덱스_재색인(현재_지라이슈인덱스, 백업_지라이슈인덱스)) {
                로그.info("인덱스 재색인을 완료하였습니다.");
                return true;
            }
        } else {
            로그.info("백업 인덱스 정보가 있습니다.");
            return true;
        }

        return false;
    }

    private boolean 인덱스_존재_확인(String 인덱스명) {
        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(인덱스명));
        return 인덱스작업.exists();
    }

    private boolean 인덱스_백업_생성(String 백업_지라이슈인덱스, Class<?> clazz) {
        IndexOperations 백업_인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(백업_지라이슈인덱스));
        Document 매핑정보 = 백업_인덱스작업.createMapping(clazz);
        백업_인덱스작업.create();
        백업_인덱스작업.putMapping(매핑정보);

        return 백업_인덱스작업.exists();
    }

    private boolean 인덱스_재색인(String 현재_지라이슈인덱스, String 백업_지라이슈인덱스) {
        boolean 결과 = false;
        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices(현재_지라이슈인덱스);
        reindexRequest.setDestIndex(백업_지라이슈인덱스);
        reindexRequest.setSourceBatchSize(5000);

        try {
            long startTime = System.currentTimeMillis();

            TaskSubmissionResponse taskSubmissionResponse = client.submitReindexTask(reindexRequest, RequestOptions.DEFAULT);
            String taskId = taskSubmissionResponse.getTask();
            String[] taskParts = taskId.split(":");
            String node = taskParts[0];
            String task = taskParts[1];

            결과 = 작업_완료_확인(node, Long.parseLong(task));

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            로그.info("Reindex 작업에 걸린 시간: " + elapsedTime + "밀리초");

            if (결과) {
                결과 = 인덱스삭제(지라이슈.class);
            }
        }
        catch (IOException | InterruptedException e) {
            로그.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return 결과;
    }

    private boolean 작업_완료_확인(String node, long task) throws IOException, InterruptedException {
        boolean 결과 = false;
        while (!결과) {
            GetTaskRequest getTaskRequest = new GetTaskRequest(node, task);
            Optional<GetTaskResponse> getTaskResponseOptional = client.tasks().get(getTaskRequest, RequestOptions.DEFAULT);

            if (getTaskResponseOptional.isPresent()) {
                GetTaskResponse getTaskResponse = getTaskResponseOptional.get();
                결과 = getTaskResponse.isCompleted();
            }

            if (결과) {
                break;
            }
            else {
                Thread.sleep(1000);
            }

        }

        return 결과;
    }
}
