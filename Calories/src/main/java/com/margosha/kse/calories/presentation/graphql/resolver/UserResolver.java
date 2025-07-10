package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.data.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
public class UserResolver {

    @EntityMapping
    public User user(Map<String, Object> representation) {
        String id = (String) representation.get("id");
        log.info("Resolving User entity with id: {}", id);
        User user = new User();
        user.setId(UUID.fromString(id));
        return user;
    }
}
