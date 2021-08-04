package com.santander.services;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import com.santander.model.User;
import com.santander.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersRepository usersRepository;

    @Override
    public Flux<User> findAll() {
        return usersRepository.findAll().delayElements(Duration.ofSeconds(1));
    }

    @Override
    public Mono<User> save(User user) {
        user.setFechaCreacion(LocalDateTime.now());
        user.setIdeUser(UUID.randomUUID().toString());
        return usersRepository.save(user);
    }

    @Override
    public Mono<User> findById(String id) {
        return usersRepository.findById(id);
    }
    @Override
    @ReactiveRedisCacheable( timeout =100 ,key = "payrollCatalog", cacheName = "mxbaasorechestration:pyme:payroll:")
    //@Cacheable(cacheNames = "my-redis-cache1",key = "'cache1-'.concat(#name)") // Use el tiempo de vencimiento 1, prueba OK
    public Mono<User> findByName(String name) {
        return usersRepository.findByName(name);
    }

    @Override
    public Mono<Void> delete(User user) {
        return usersRepository.delete(user);
    }
}
