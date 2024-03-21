package com.arms.api.alm.issueresolution.controller;

import com.arms.api.alm.issueresolution.model.지라이슈해결책_데이터;
import com.arms.api.alm.issueresolution.service.지라이슈해결책_전략_호출;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/{connectId}/jira/issueresolution")
public class 지라이슈해결책_컨트롤러 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    지라이슈해결책_전략_호출 지라이슈해결책_전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈해결책_데이터> 이슈해결책_목록_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                            ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 이슈해결책_목록_가져오기");

        return 지라이슈해결책_전략_호출.이슈해결책_목록_가져오기(연결_아이디);
    }
}
