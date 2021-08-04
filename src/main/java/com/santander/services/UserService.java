package com.santander.services;

import com.santander.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    public Flux<User> findAll();
    public Mono<User> save(User user);
    public Mono<User> findById(String id);
    public Mono<User> findByName(String name);
    public Mono<Void> delete(User user);
}
