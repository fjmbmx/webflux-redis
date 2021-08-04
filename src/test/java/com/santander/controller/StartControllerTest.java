package com.santander.controller;

import com.santander.model.Profile;
import com.santander.model.User;
import com.santander.services.ProfileService;
import com.santander.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StartControllerTest {
    /**
     * Objeto que simula las peticiones a los endPoints
     */
    @LocalServerPort
    protected int serverPort;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    UserService userService;
    @Autowired
    ProfileService profileService;

    @Value("${config.base.endpoint}")
    private String url;

    @Test
    public void getContacts() {
        RestTemplate restTemplate = new RestTemplate();
        String resultado = restTemplate.getForObject("http://localhost:" + serverPort + "/users", String.class);
        System.out.println("Lista de Users : " + resultado);
    }

    @Test
    public void createUser() {
        Profile profile = profileService.findByName("Admin").block();
        User user = new User("Francisco", profile, "Martinez", "29", "fjmb@gmail.com");

        RestTemplate restTemplate = new RestTemplate();
        User resultado = restTemplate.postForObject("http://localhost:" + serverPort + "/user", user, User.class);
        System.out.println("New User : " + resultado.getLastName());
    }

    @Test
    public void crearUser() {
        Profile profile = profileService.findByName("Admin").block();
        User user = new User("Francisco", profile, "Martinez", "29", "fjmb@gmail.com");

        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(user), User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Francisco");

    }

    @Test
    public void listarWebClient() {
        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBodyList(User.class)
                .consumeWith(response ->
                {
                    List<User> usuarios = response.getResponseBody();
                    usuarios.forEach(user -> System.out.println(user.getName() + " " + user.getLastName()));
                    Assertions.assertThat(usuarios.size() > 0).isTrue();
                });
    }

    @Test
    public void verDetalleWebClient() {
        User user = userService.findByName("Miguel").block();
        webTestClient.get()
                .uri("/api/v2/users/{id}", Collections.singletonMap("id", user.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(User.class)
                .consumeWith(response -> {
                    User u = response.getResponseBody();
                    Assertions.assertThat(u.getIdeUser()).isNotEmpty();
                    Assertions.assertThat(u.getAge()).isEqualTo("35");
                    Assertions.assertThat(u.getId().equals(user.getId())).isTrue();
                });
    }
    @Test
    public void editUserWebClient() {
        Profile profile = profileService.findByName("Admin").block();
        User user = userService.findByName("Miguel").block();
        User userNew = new User("Jose Miguel", profile, "Martinez Bautista", "37", "miguesoft@gmail.com");

        webTestClient.put()
                .uri(url+"/{id}",Collections.singletonMap("id",user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(userNew), User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.lastName").isEqualTo("Martinez Bautista");
    }

    @Test
    public void eliminarTest() {
        User user = userService.findByName("Uriel").block();
        webTestClient.delete()
                .uri(url + "/{id}", Collections.singletonMap("id", user.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

        webTestClient.get()
                .uri(url + "/{id}", Collections.singletonMap("id", user.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }
}
