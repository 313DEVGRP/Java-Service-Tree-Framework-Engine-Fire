package com.arms.api.alm.admin.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
class FluentdIndexAdminServiceTest {

    @Autowired
    FluentdIndexAdminService fluentdIndexAdminService;

    @Test
    public void test(){
        fluentdIndexAdminService.delete();;
    }
}