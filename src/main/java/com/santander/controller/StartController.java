package com.santander.controller;

import com.santander.model.User;
import com.santander.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
public class StartController {

    @Autowired
    private UserService userService;
    @Value("${config.uploads.path}")
    private String path;

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping("/user")
    public Mono<User> createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PostMapping(value = "/user/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<User>> updateImgenUser(@PathVariable String id,@RequestPart(value = "image", required = false) MultipartFile file) {

       return  userService.findById(id)
                .flatMap(user -> {
                    user.setImageUri(UUID.randomUUID().toString() + "-" + file.getName()
                            .replace(" ", "")
                            .replace(":", ""));
                    try {
                        file.transferTo(new File(path + user.getImageUri() + ".jpg"));
                        return Mono.just(user);
                    } catch (IOException e) {

                    }
                    return Mono.empty();
                    //return file.transferTo(new File(path + user.getImageUri())).then(userService.save(user));
                }).flatMap(user -> userService.save(user))
                .map(userMono -> ResponseEntity.ok(userMono))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{id}")
    public Mono<ResponseEntity<User>> editar(@RequestBody User user, @PathVariable String id) {

        return userService.findById(id)
                .flatMap(userUpdate -> {
                    userUpdate.setName(user.getName());
                    userUpdate.setAge(user.getAge());
                    userUpdate.setEmail(user.getEmail());
                    userUpdate.setPhone(user.getPhone());
                    return userService.save(userUpdate);
                }).map(u -> ResponseEntity.created(URI.create("/users/".concat(u.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(u))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/v2/{name}")
    public Mono<ResponseEntity<User>> getUserByName(@PathVariable(value = "name") String name) {
        return userService.findByName(name)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @GetMapping("/user/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable(value = "id") String id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/user/{id}")
    public Mono<ResponseEntity<Boolean>> deleteUser(@PathVariable(value = "id") String id) {
        Mono<ResponseEntity<Boolean>> responseEntityMono = userService.findById(id)
                .flatMap(
                        user ->
                                userService.delete(user)
                                        .then(Mono.just(ResponseEntity.status(HttpStatus.OK).body(Boolean.TRUE)))
                )
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Boolean.FALSE));
        return responseEntityMono;
    }
}
