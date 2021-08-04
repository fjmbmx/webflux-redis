package com.santander;

import com.santander.model.Profile;
import com.santander.model.User;
import com.santander.repository.UsersRepository;
import com.santander.services.ProfileService;
import com.santander.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@EnableEurekaClient
@EnableReactiveMongoRepositories(basePackageClasses = UsersRepository.class)
@SpringBootApplication()
public class SantanderServiceApplication implements CommandLineRunner {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    @Autowired
    ProfileService profileService;
    @Autowired
    UserService userService;
    private static final Logger log = LoggerFactory.getLogger(SantanderServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SantanderServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        mongoTemplate.dropCollection("profiles").subscribe();
        mongoTemplate.dropCollection("users").subscribe();

        Profile p1 = new Profile("Admin");
        Flux.just(p1)
                .flatMap(profileService::save)
                .doOnNext(c -> {
                    log.info("Profile creada: " + c.getName() + ", Id: " + c.getId());
                }).thenMany(
                Flux.just(new User("1", "Miguel", p1, "Martinez", "35", "miguesoft@gmail.com"),
                        new User("2", "Uriel", p1, "Martinez", "35", "miguesoft@gmail.com"))
                        .flatMap(user -> {
                            user.setFechaCreacion(LocalDateTime.now());
                            return userService.save(user);
                        })
        )
                .subscribe(user -> log.info("Insert: " + user.getId() + " " + user.getProfile().getName()));
    }
}
