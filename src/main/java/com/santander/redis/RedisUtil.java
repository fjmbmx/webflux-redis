package com.santander.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Herramientas CURD de operación de Redis
 * Created by hesh on 2018/8/26.
 */
@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /********************* Objetos de caché, el más utilizado, adecuado para todos los tipos ****************** **** /

     /**
     * Escribir en caché sin tiempo de caducidad
     *
     * @param key key
     * Objeto de valor @param
     * @return
     */
    public boolean set(String key, Object value) {
        try {
            // Debido a que redisTemplate se ha serializado, no es necesario volver a configurar
//ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Excepción de caché de valor de escritura, {}", e);
        }
        return false;
    }

    /**
     * Escribir en caché, usar el tiempo de vencimiento
     *
     * @param key
     * @param value
     * @param expireTime tiempo de vencimiento
     * @param timeUnit   unidad
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        try {
//ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            ValueOperations operations = redisTemplate.opsForValue();
//operations.set(key, value);
//redisTemplate.expire(key, expireTime, timeUnit);
            operations.set(key, value, expireTime, timeUnit);
            return true;
        } catch (Exception e) {
            log.error("Excepción de caché de valor de escritura, {}", e);
        }
        return false;
    }

    /**
     * Leer caché
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        // ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        ValueOperations operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }


    /**
     * Determinar si hay un valor correspondiente en la caché
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * Eliminar el valor correspondiente
     *
     * @param key
     */
    public boolean remove(final String key) {
        if (exists(key)) {
            return redisTemplate.delete(key);
        } else {
            return false;
        }
    }

    /**
     * Eliminar el valor correspondiente en lote
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * Según las expresiones regulares, elimine claves en lotes
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /********************* Las siguientes son operaciones básicas de hash, lista, etc. ***************** ******/
    public boolean hmSet(String key, Object hk, Object hv) {
        try {
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            hash.put(key, hk, hv);
            return true;
        } catch (Exception e) {
            log.error("Escritura en la excepción de caché hash, {}", e);
        }
        return false;
    }

    /**
     * Hash para obtener datos
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * Adición de lista
     *
     * @param k
     * @param v
     */
    public void listSet(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    /**
     * Lista de acceso
     *
     * @param k
     * @param start
     * @param end
     * @return
     */
    public List<Object> listGet(String k, long start, long end) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, start, end);
    }

    /**
     * Colección agregar
     *
     * @param key
     * @param value
     */
    public void setAdd(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * Adquisición de colecciones
     *
     * @param key
     * @return
     */
    public Set<Object> setPop(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * Colección ordenada agregada
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zSetAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * Adquisición de colección ordenada
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> zSetPop(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

}