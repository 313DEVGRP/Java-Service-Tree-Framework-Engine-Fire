package com.arms.api.jira.user.factory;

import com.arms.api.jira.user.service.strategy.UserStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserFactory {
    private final Map<String, UserStrategy> strategies;

    public UserFactory(List<UserStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(UserStrategy::getType, Function.identity()));
    }

    public UserStrategy getStrategy(String type) {
        return strategies.get(type.toLowerCase());
    }
}
