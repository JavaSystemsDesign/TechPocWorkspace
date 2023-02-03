package com.pg.spring.reactive.repository;

import com.pg.spring.reactive.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveCrudRepository<User,Integer> {
    @Query("select * from user where age >= :age")
    Flux<User> findByAge(int age);
}
