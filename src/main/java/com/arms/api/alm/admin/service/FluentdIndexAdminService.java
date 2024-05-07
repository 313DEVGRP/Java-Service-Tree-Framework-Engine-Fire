package com.arms.api.alm.admin.service;


import com.arms.api.alm.admin.constrant.ServiceName;
import com.arms.api.alm.fluentd.repository.플루언트디_저장소;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class FluentdIndexAdminService implements IndexAdminService {

    private final 플루언트디_저장소 fluentdRepository;

    @Override
    public void delete() {
        Set<String> fluentd = fluentdRepository.findIndexNamesByAlias(IndexCoordinates.of("fluentd"));
        fluentd.stream()
            .filter(indexName->!twoDayList(indexName))
            .collect(Collectors.toList())
            .forEach(indexName-> {
                fluentdRepository.deleteIndex(IndexCoordinates.of(indexName));
            }
        );
    }

    private boolean twoDayList(String indexName){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.DATE, -1);
        Date twoDaysAgo = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        calendar.setTime(twoDaysAgo);

        return IntStream.range(0,2).boxed().map(a->{
            String day = formatter.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            return "fluentd-"+day;
        }).collect(Collectors.toList()).contains(indexName);

    }

    @Override
    public ServiceName serviceName() {
        return ServiceName.FLUENTD;
    }
}
