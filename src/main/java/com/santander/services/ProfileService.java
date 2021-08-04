package com.santander.services;

import com.santander.model.Profile;
import com.santander.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProfileService {

    public Mono<Profile> findByName(String profileName);

    public Mono<Profile> save(Profile profile);
}
