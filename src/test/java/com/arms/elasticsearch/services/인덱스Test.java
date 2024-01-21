package com.arms.elasticsearch.services;

import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.repositories.인덱스자료;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.tasks.GetTaskRequest;
import org.elasticsearch.client.tasks.GetTaskResponse;
import org.elasticsearch.client.tasks.TaskSubmissionResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("dev")
public class 인덱스Test {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    private ElasticsearchOperations 엘라스틱서치_작업;


    @Test
    public void 인덱스백업Test() {
        인덱스백업(지라이슈.class);
    }

    @Test
    public void 인덱스삭제Test() {
        인덱스삭제();

    }

    @Test
    public void 인덱스백업삭제Test() {
        인덱스백업(지라이슈.class);
    }

    public boolean 인덱스삭제() {
        boolean 삭제결과 = false;
        String 현재_지라이슈인덱스 = 인덱스자료.지라이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(2023-11-28));
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "-2023-11-30";

        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(백업_지라이슈인덱스));

        try {
            삭제결과 = 인덱스작업.delete();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return 삭제결과;
    }

    public boolean 인덱스백업(Class<?> clazz) {
        String 현재_지라이슈인덱스 = 인덱스자료.지라이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "-2023-12-01";

        if (!인덱스_존재_확인(백업_지라이슈인덱스)) {
            if (!인덱스_존재_확인(현재_지라이슈인덱스)) {
                System.out.println("현재 인덱스 정보가 없습니다.");
                return true;
            }

            if (!인덱스_백업_생성(백업_지라이슈인덱스, clazz)) {
                return false;
            }

            if(인덱스_재색인(현재_지라이슈인덱스, 백업_지라이슈인덱스)) {
                System.out.println("인덱스 재색인을 완료하였습니다.");
                return true;
            }
        } else {
            System.out.println("백업 인덱스 정보가 있습니다.");
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
            System.out.println("Reindex 작업에 걸린 시간: " + elapsedTime + "밀리초");

            // 결과 = 인덱스삭제();
        } catch (IOException | InterruptedException e) {
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

    @Test
    public void 인덱스TestTest() {
        인덱스백업삭제RequestTest();
    }

    boolean 인덱스백업삭제RequestTest() {
        String 현재_지라이슈인덱스 = 인덱스자료.지라이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "-2023-12-01";

        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices(현재_지라이슈인덱스);
        reindexRequest.setDestIndex(백업_지라이슈인덱스);

        try {
            BulkByScrollResponse reindexResponse = client.reindex(reindexRequest, RequestOptions.DEFAULT);
            if (reindexResponse.getBulkFailures().size() == 0) {
                // Reindex 작업 성공 시, DeleteRequest 생성
                // DeleteIndexRequest deleteRequest = new DeleteIndexRequest(현재_지라이슈인덱스+"-2023-11-29");

                // Delete 실행
                // client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
                // System.out.println("Index deleted successfully!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
