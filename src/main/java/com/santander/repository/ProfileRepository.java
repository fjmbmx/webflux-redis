package com.santander.repository;

import com.santander.model.Profile;
import com.santander.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile,String> {
    public Mono<Profile> findByName(String nameProfile);
    @Query("{ 'name': ?0 }")
    public Mono<Profile> obtenerPorNameProfile(String nameProfile);
}
