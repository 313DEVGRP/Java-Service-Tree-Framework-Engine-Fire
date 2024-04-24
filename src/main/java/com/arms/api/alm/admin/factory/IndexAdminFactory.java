package com.arms.api.alm.admin.factory;

import com.arms.api.alm.admin.constrant.ServiceName;
import com.arms.api.alm.admin.service.IndexAdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IndexAdminFactory {

    private final IndexAdminServices indexAdminServices;

    public IndexAdminService findService(ServiceName serviceName){
        return indexAdminServices.findService(serviceName);
    }

}
