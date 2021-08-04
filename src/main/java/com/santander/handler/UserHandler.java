package com.santander.handler;

import com.santander.model.User;
import com.santander.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class UserHandler {
    @Autowired
    private UserService userService;

    @Autowired
    Validator validator;

    @Value("${config.uploads.path}")
    private String path;

    public Mono<ServerResponse> listar(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll(), User.class);
    }

    public Mono<ServerResponse> upload(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData().map(multipartFile -> multipartFile.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> userService.findById(id)
                        .flatMap(user -> {
                            user.setImageUri(UUID.randomUUID().toString() + "-" + filePart.filename()
                                    .replace(" ", "")
                                    .replace(":", ""));
                            return filePart.transferTo(new File(path + user.getImageUri() + ".jpg")).then(
                                    userService.save(user)
                            );
                        })
                ).flatMap(u -> ServerResponse.created(URI.create("/api/v2/users/upload/".concat(u.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(u)))
                .switchIfEmpty(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> ver(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return userService.findById(id).flatMap(user ->
                ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(user))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> crear(ServerRequest serverRequest) {
        Mono<User> monoUser = serverRequest.bodyToMono(User.class);
        return monoUser.flatMap(user -> {
            Errors errors = new BeanPropertyBindingResult(user, User.class.getName());
            validator.validate(user, errors);
            if (errors.hasErrors()) {
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldError -> "El campo" + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().body(fromObject(list)));
            } else {
                if (user.getFechaCreacion() == null) {
                    user.setFechaCreacion(LocalDateTime.now());
                }
                return userService.save(user)
                        .flatMap(userSaveBd -> ServerResponse.created(URI.create("/api/v2/users".concat(userSaveBd.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromObject(userSaveBd)));
            }
        });
    }

    public Mono<ServerResponse> crearConFoto(ServerRequest serverRequest) {
        Mono<User> monoUser = serverRequest.multipartData().map(multipart ->
        {
            FormFieldPart ffpName = (FormFieldPart) multipart.toSingleValueMap().get("name");
            FormFieldPart ffpLastName = (FormFieldPart) multipart.toSingleValueMap().get("lastName");
            FormFieldPart ffpAge = (FormFieldPart) multipart.toSingleValueMap().get("age");
            FormFieldPart ffpEmail = (FormFieldPart) multipart.toSingleValueMap().get("email");
            FormFieldPart ffpPhone = (FormFieldPart) multipart.toSingleValueMap().get("phone");
            User u = new User(ffpName.value(), null, ffpLastName.value(), ffpAge.value(), ffpEmail.value());

            u.setName(ffpName.value());
            u.setLastName(ffpLastName.value());
            u.setAge(ffpAge.value());
            u.setEmail(ffpEmail.value());
            u.setPhone(ffpPhone.value());
            return u;
        });
        return serverRequest.multipartData().map(multipartFile -> multipartFile.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> monoUser
                        .flatMap(user -> {
                            user.setImageUri(UUID.randomUUID().toString() + "-" + filePart.filename()
                                    .replace(" ", "")
                                    .replace(":", ""));
                            user.setFechaCreacion(LocalDateTime.now());
                            return filePart.transferTo(new File(path + user.getImageUri() + ".jpg")).then(
                                    userService.save(user)
                            );
                        })
                ).flatMap(u -> ServerResponse.created(URI.create("/api/v2/users/upload/".concat(u.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(u)));
    }

    public Mono<ServerResponse> actualizar(ServerRequest serverRequest) {
        Mono<User> monoUser = serverRequest.bodyToMono(User.class);
        String id = serverRequest.pathVariable("id");
        Mono<User> userDb = userService.findById(id);
        return userDb.zipWith(monoUser, (db, req) -> {
            db.setName(req.getName());
            db.setPhone(req.getPhone());
            db.setEmail(req.getEmail());
            db.setAge(req.getAge());
            db.setLastName(req.getLastName());
            db.setImageUri(req.getImageUri());
            return db;
        }).flatMap(user -> ServerResponse.created(URI.create("/api/v2/users".concat(user.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.save(user), User.class));
    }

    public Mono<ServerResponse> eliminar(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<User> userDb = userService.findById(id);
        return userDb.flatMap(user -> userService.delete(user).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
