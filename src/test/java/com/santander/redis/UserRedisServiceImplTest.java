package com.santander.redis;

import com.santander.model.Profile;
import com.santander.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by He on 2018/8/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserRedisServiceImplTest {

    /* Prueba usar redisTemplate directamente para usar redis para manipular datos */

    @Autowired
    private RedisUtil redisUtil;

    /* Probar objeto de caché */
    @Test
    public void setAndGet() {
        redisUtil.set("test:value:setString", "value1");
        Assert.assertEquals("value1", redisUtil.get("test:value:setString"));
    }

    @Test
    // Usa redisTemplate para acceder a los objetos directamente
    public void setAndGetUser() {
        Profile p1 = new Profile("Admin");
        User user = new User("2", "Miguel", p1, "Martinez", "35", "miguesoft@gmail.com");
        redisUtil.set("test:value:setObject", user);
        User userTest = (User) redisUtil.get("test:value:setObject");
        log.info("Obtener objeto: {}", userTest.toString());
    }

    /* Usar tiempo de vencimiento */
    @Test
    public void testTimeOut() {
        redisUtil.set("test:value:setTimeout", "timeout", Long.valueOf(20), TimeUnit.SECONDS);
        log.info("Primera vez para obtener el objeto: {}", redisUtil.get("test:value:setTimeout"));
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("El segundo objeto de adquisición: {}", redisUtil.get("test:value:setTimeout"));
    }

    /* ¿Existe, y eliminar */
    @Test
    public void testDeleteExist() {
        boolean isExist = redisUtil.exists("test:value:setString");
        log.info("¿El valor existe? {}", isExist);
        if (isExist) {
            boolean isDeleted = redisUtil.remove("test:value:setString");
            log.info("¿Eliminar correctamente? {}", isDeleted);
        }
    }


    /**
     * hash
     */
    @Test
    public void testHash() {
        Profile p1 = new Profile("Admin");
        User user = new User("1", "Miguel", p1, "Martinez", "35", "miguesoft@gmail.com");
        redisUtil.hmSet("test:hash:setObject:", "user1", user);
        Assert.assertEquals("Miguel", ((User) redisUtil.hmGet("test:hash:setObject:", "user1")).getName());
    }


    /**
     * list
     */
    @Test
    public void testList() {
        Random random = new Random();

        Profile p1 = new Profile("Admin");
        List<User> users = new ArrayList<>();
        users.add(new User(String.valueOf(random.nextInt()), "Miguel", p1, "Martinez", "35", "miguesoft@gmail.com"));
        users.add(new User(String.valueOf(random.nextInt()), "Uriel", p1, "Martinez", "35", "miguesoft@gmail.com"));

        redisUtil.listSet("test:list:setObject:", users);
        Assert.assertNotNull(redisUtil.listGet("test:list:setObject:", 0, 0));
    }

    /**
     * set
     */
    @Test
    public void testSet() {
        Profile p1 = new Profile("Admin");
        User user = new User("1", "Miguel", p1, "Martinez", "35", "miguesoft@gmail.com");
        Set<User> users = new HashSet<>();
        users.add(user);
        redisUtil.setAdd("test:set:setObject:", users);
        Assert.assertNotNull(redisUtil.setPop("test:set:setObject:"));
    }

/**
 * zSet
 */
// @Test
// public void testZSet() {
// User user1 = new User("list1", 12);
// User user2 = new User("list1", 11);
// LinkedHashSet<User> users = new LinkedHashSet<>();
// users.add(user1);
// users.add(user2);
// redisUtil.zSetAdd("test:ZSet:setObject:",users,2);
// Assert.assertNotNull(redisUtil.zSetPop("test:ZSet:setObject:",2,1));
// }
}
