package com.santander.redis;

import com.santander.model.Profile;
import com.santander.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
//@CacheConfig(cacheNames = "User",keyGenerator = "keyGenerator")
public class UserRedisServiceImpl implements UserRedisService {

    @Override
    @Cacheable(cacheNames = "my-redis-cache1",keyGenerator = "customKeyGenerator") // Use el tiempo de vencimiento 1, prueba OK
    public List<User> getUser(String username,  PageRequest pageRequest) {
        log.info ("Ingrese la clase de implementación para obtener datos: {}" +username);
        Random random = new Random();

        Profile p1= new Profile("Admin");
        List<User> users = new ArrayList<>();
        users.add( new User(String.valueOf(random.nextInt()),"Miguel",p1,"Martinez","35","miguesoft@gmail.com"));
        users.add(new User(String.valueOf(random.nextInt()),"Uriel",p1,"Martinez","35","miguesoft@gmail.com"));
        return users;
    }


    //@Cacheable (cacheNames = "my-redis-cache2", key = "'cache2-'.concat(#username)") // Usa el tiempo de vencimiento 2, prueba OK
    @Override
    public User getUser2(String username) {
        log.info ("Ingrese la clase de implementación para obtener datos: {}" +username);
        Random random = new Random();
        int age = random.nextInt(30);
        if (age < 20) {
            return null; // La prueba devuelve vacío, sin estrategia de caché
        }else {
            Profile p1= new Profile("Admin");
            return new  User(String.valueOf(random.nextInt()),"Uriel",p1,"Martinez",String.valueOf(age),"miguesoft@gmail.com");
        }

    }
}