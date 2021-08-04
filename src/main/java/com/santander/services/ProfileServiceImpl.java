package com.santander.services;

import com.santander.model.Profile;
import com.santander.model.User;
import com.santander.repository.ProfileRepository;
import com.santander.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Override
    public Mono<Profile> findByName(String profileName) {
        return profileRepository.obtenerPorNameProfile(profileName);
    }
    @Override
    public Mono<Profile> save(Profile profile) {
        return profileRepository.save(profile);
    }
}
