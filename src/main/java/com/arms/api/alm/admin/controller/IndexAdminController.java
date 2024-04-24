package com.arms.api.alm.admin.controller;

import com.arms.api.alm.admin.constrant.ServiceName;
import com.arms.api.alm.admin.factory.IndexAdminFactory;
import com.arms.api.utils.response.응답처리;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.arms.api.utils.response.응답처리.success;


@RequestMapping("/engine/index/")
@AllArgsConstructor
@RestController
public class IndexAdminController {

    private final IndexAdminFactory indexAdminFactory;

    @ResponseBody
    @PostMapping("/delete")
    public ResponseEntity<응답처리.ApiResult<String>> indexDelete(String serviceName)  {
        indexAdminFactory.findService(ServiceName.valueOf(serviceName)).delete();
        return ResponseEntity.ok(success("OK"));
    }

}
