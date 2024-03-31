package com.arms.api.engine.controller;

import com.arms.api.engine.model.dto.검색어_기본_검색_요청;
import com.arms.api.engine.model.dto.검색어_날짜포함_검색_요청;
import com.arms.api.engine.model.dto.검색어_집계_요청;
import com.arms.api.engine.model.entity.지라이슈;
import com.arms.api.engine.model.entity.플루언트디;
import com.arms.api.engine.model.vo.검색어_검색결과;
import com.arms.api.engine.service.지라이슈_대시보드_서비스;
import com.arms.api.engine.service.플루언트디_서비스;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/engine/search")
@Slf4j
public class 엔진_검색_컨트롤러 {

    @Autowired
    private 지라이슈_대시보드_서비스 지라이슈_검색엔진;

    @Autowired
    private 플루언트디_서비스 플루언트디_서비스;

    @GetMapping("/jiraissue")
    public ResponseEntity<?> 검색엔진_지라이슈_검색(@RequestParam("search_string") String 검색어,
                                          @RequestParam("page") String 페이지,
                                          @RequestParam("size") String 크기) {
        log.info("[엘라스틱_지라이슈_대시보드_컨트롤러 :: 검색엔진_지라이슈_검색] :: 검색어 => {}" , 검색어);
        검색어_기본_검색_요청 검색어_기본_검색_요청 = new 검색어_기본_검색_요청();
        검색어_기본_검색_요청.set검색어(검색어);
        검색어_기본_검색_요청.set페이지(Integer.parseInt(페이지));
        검색어_기본_검색_요청.set크기(Integer.parseInt(크기));
        검색어_검색결과<SearchHit<지라이슈>> 검색결과_목록 = 지라이슈_검색엔진.지라이슈_검색(검색어_기본_검색_요청);
        return ResponseEntity.ok(검색결과_목록);
    }

    @GetMapping("/jiraissue/with-date")
    public ResponseEntity<?> 검색엔진_지라이슈_날짜포함_검색(@RequestParam("search_string") String 검색어,
                                               @RequestParam("page") String 페이지,
                                               @RequestParam("size") String 크기,
                                               @RequestParam(value = "from",required = false) String 시작_날짜,
                                               @RequestParam(value = "to",  required = false) String 끝_날짜) {
        log.info("[엘라스틱_지라이슈_대시보드_컨트롤러 :: 검색엔진_지라이슈_검색] :: 검색어 => {}" , 검색어);
        검색어_날짜포함_검색_요청 검색어_날짜포함_검색_요청 = new 검색어_날짜포함_검색_요청();
        검색어_날짜포함_검색_요청.set검색어(검색어);
        검색어_날짜포함_검색_요청.set페이지(Integer.parseInt(페이지));
        검색어_날짜포함_검색_요청.set크기(Integer.parseInt(크기));
        검색어_날짜포함_검색_요청.set시작_날짜(시작_날짜);
        검색어_날짜포함_검색_요청.set끝_날짜(끝_날짜);
        검색어_검색결과<SearchHit<지라이슈>> 검색결과_목록 = 지라이슈_검색엔진.지라이슈_날짜포함_검색(검색어_날짜포함_검색_요청);
        return ResponseEntity.ok(검색결과_목록);
    }


    @GetMapping("/log")
    public ResponseEntity<?> 검색엔진_플루언트디_검색(@RequestParam("search_string") String 검색어,
                                           @RequestParam("page") String 페이지,
                                           @RequestParam("size") String 크기) {
        log.info("[엘라스틱_지라이슈_대시보드_컨트롤러 :: 검색엔진_플루언트디_검색] :: 검색어 => {}" , 검색어);
        검색어_기본_검색_요청 검색어_기본_검색_요청 = new 검색어_기본_검색_요청();
        검색어_기본_검색_요청.set검색어(검색어);
        검색어_기본_검색_요청.set페이지(Integer.parseInt(페이지));
        검색어_기본_검색_요청.set크기(Integer.parseInt(크기));
        검색어_검색결과<SearchHit<플루언트디>> 검색결과_목록 = 플루언트디_서비스.플루언트디_검색(검색어_기본_검색_요청);
        return ResponseEntity.ok(검색결과_목록);
    }

    @GetMapping("/log/with-date")
    public ResponseEntity<?> 검색엔진_플루언트디_날짜포함_검색(@RequestParam("search_string") String 검색어,
                                                @RequestParam("page") String 페이지,
                                                @RequestParam("size") String 크기,
                                                @RequestParam(value = "from",required = false) String 시작_날짜,
                                                @RequestParam(value = "to",  required = false) String 끝_날짜) {
        log.info("[엘라스틱_지라이슈_대시보드_컨트롤러 :: 검색엔진_지라이슈_검색] :: 검색어 => {}" , 검색어);
        검색어_날짜포함_검색_요청 검색어_날짜포함_검색_요청 = new 검색어_날짜포함_검색_요청();
        검색어_날짜포함_검색_요청.set검색어(검색어);
        검색어_날짜포함_검색_요청.set페이지(Integer.parseInt(페이지));
        검색어_날짜포함_검색_요청.set크기(Integer.parseInt(크기));
        검색어_날짜포함_검색_요청.set시작_날짜(시작_날짜);
        검색어_날짜포함_검색_요청.set끝_날짜(끝_날짜);
        검색어_검색결과<SearchHit<플루언트디>> 검색결과_목록 = 플루언트디_서비스.플루언트디_날짜포함_검색(검색어_날짜포함_검색_요청);
        return ResponseEntity.ok(검색결과_목록);
    }


    @GetMapping("/log-aggs-top5/with-date")
    public ResponseEntity<버킷_집계_결과_목록_합계> 검색엔진_플루언트디_로그네임_집계_top5(@RequestParam("search_string") String 검색어,
                                                                  @RequestParam(value = "from",required = false) String 시작_날짜,
                                                                  @RequestParam(value = "to",  required = false) String 끝_날짜) {
        log.info("[엘라스틱_지라이슈_대시보드_컨트롤러 :: 검색엔진_플루언트디_로그네임_집계_top5]");
        검색어_집계_요청 집계_요청 = new 검색어_집계_요청();
        집계_요청.set검색어(검색어);
        집계_요청.set시작_날짜(시작_날짜);
        집계_요청.set끝_날짜(끝_날짜);
        집계_요청.set컨텐츠보기여부(false);
        집계_요청.set페이지(0);
        집계_요청.set크기(5);
        집계_요청.set메인그룹필드("@log_name");

        버킷_집계_결과_목록_합계 집계_결과 = 플루언트디_서비스.플루언트디_로그네임_집계(집계_요청);

        return ResponseEntity.ok(집계_결과);
    }

    @GetMapping("/project-aggs-top5/with-date")
    public ResponseEntity<버킷_집계_결과_목록_합계> 검색엔진_이슈_프로젝트명_집계_top5(@RequestParam("search_string") String 검색어,
                                                                  @RequestParam(value = "from",required = false) String 시작_날짜,
                                                                  @RequestParam(value = "to",  required = false) String 끝_날짜) {
        log.info("[엘라스틱_지라이슈_대시보드_컨트롤러 :: 검색엔진_플루언트디_로그네임_집계_top5]");
        검색어_집계_요청 집계_요청 = new 검색어_집계_요청();
        집계_요청.set검색어(검색어);
        집계_요청.set시작_날짜(시작_날짜);
        집계_요청.set끝_날짜(끝_날짜);
        집계_요청.set컨텐츠보기여부(false);
        집계_요청.set페이지(0);
        집계_요청.set크기(5);
        집계_요청.set메인그룹필드("project.project_name.keyword");

        버킷_집계_결과_목록_합계 집계_결과 = 지라이슈_검색엔진.이슈_프로젝트명_집계(집계_요청);

        return ResponseEntity.ok(집계_결과);
    }
}
