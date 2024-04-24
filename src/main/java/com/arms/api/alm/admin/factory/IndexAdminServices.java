package com.arms.api.alm.admin.factory;

import com.arms.api.alm.admin.constrant.ServiceName;
import com.arms.api.alm.admin.service.IndexAdminService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class IndexAdminServices {
    private final Map<ServiceName, IndexAdminService> indexAdminServiceMap;

    public IndexAdminServices(Set<IndexAdminService> indexAdminServices) {
        this.indexAdminServiceMap = new HashMap<>();
        indexAdminServices.forEach(
            service->indexAdminServiceMap.put(service.serviceName(),service)
        );
    }
    public IndexAdminService findService(ServiceName serviceName){
        return indexAdminServiceMap.get(serviceName);
    }


}
