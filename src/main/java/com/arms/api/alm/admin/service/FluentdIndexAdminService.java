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
import java.util.List;
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
        List<String> days = dayList(90);
        fluentd.stream()
            .filter(indexName->!days.contains(indexName))
            .collect(Collectors.toList())
            .forEach(indexName-> {
                log.info("delete-index->{}",indexName);
                fluentdRepository.deleteIndex(IndexCoordinates.of(indexName));
            }
        );
    }

    private List<String> dayList(Integer range) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.DATE, -(Math.abs(range) - 1));
        Date twoDaysAgo = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        calendar.setTime(twoDaysAgo);

        return IntStream.range(0, range).boxed().map(a -> {
            String day = formatter.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            return "fluentd-" + day;
        }).collect(Collectors.toList());

    }

    @Override
    public ServiceName serviceName() {
        return ServiceName.FLUENTD;
    }
}
