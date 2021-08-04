package com.santander.repository;

import com.santander.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UsersRepository extends ReactiveMongoRepository<User,String> {
    public Mono<User> findByName(String name);
}
