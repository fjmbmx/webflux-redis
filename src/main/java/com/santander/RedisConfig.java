package com.santander;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by He on 2018/8/23.
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * @return clave generada por estrategia personalizada
     * @description estrategia de generación de claves de caché personalizada
     * Si desea utilizar esta clave, solo necesita establecer el valor de keyGenerator en el comentario en keyGenerator </br>
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuffer sb = new StringBuffer();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(":" + obj.toString());
            }
            return sb.toString();
        };
    }

    // Administrador de caché, solo se usa el método de anotación y el método sin anotación usa el método original para establecer parámetros
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        /*La siguiente es la forma Spring boot1.x */
        //Método 1
        // RedisCacheManager rcm = new RedisCacheManager (redisTemplate);
        // La redisTemplate aquí usa redisTemplate (fábrica)
        // Establecer el tiempo de caducidad de la caché
        // rcm.setDefaultExpiration (60); // segundo
        // return rcm;

        // Método 2
        // RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
        // .fromConnectionFactory(factory);
        // return builder.build();

        // Método 3
        // RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(factory);
        // RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        // RedisCacheConfiguration configuration = defaultCacheConfig.entryTtl(Duration.ofSeconds(100));
        // RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter,configuration);

        /*************** El siguiente es el modo Spring boot 2.x ************************/

        // Método 1, la configuración más simple
        //return RedisCacheManager.create(factory);

        /*Método 2, cuando necesita configurar el tiempo de vencimiento de cada espacio de caché específico
         * por separado, preste atención a la correspondencia de cacheName * /
         */
        //Genera una configuración predeterminada y personaliza la caché a través del objeto de configuración

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        //Necesita devolver la configuración para reasignar para que sea efectiva

        cacheConfig = cacheConfig.entryTtl(Duration.ofMinutes(2)) // Establezca el tiempo de vencimiento predeterminado del caché, también use la configuración de Duración
                .disableCachingNullValues() // No almacenar en caché los valores nulos
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer())); // Establecer serialización de valor

        // Establecer una colección de conjuntos de espacio de caché inicial, que puede utilizar diferentes estrategias de almacenamiento en caché para diferentes datos;
        // También puede usar el mismo tiempo de vencimiento para todos los contenedores de caché
        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("my-redis-cache1");
        cacheNames.add("my-redis-cache2");

        // Aplicar un tiempo de caducidad diferente a cada espacio de caché
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("my-redis-cache1", cacheConfig.entryTtl(Duration.ofSeconds(30)));
        configMap.put("my-redis-cache2", cacheConfig.entryTtl(Duration.ofSeconds(60)));

        RedisCacheManager cacheManager = RedisCacheManager.builder(factory) // Inicializar un cacheManager con una configuración de caché personalizada
                .initialCacheNames(cacheNames) // Preste atención a la secuencia de llamada de estas dos oraciones, asegúrese de llamar a este método para establecer el nombre de caché inicializado y luego inicialice la configuración relacionada
                .withInitialCacheConfigurations(configMap)
                .build();
        return cacheManager;

        // ----- Método 3, simplemente configure un tiempo de vencimiento unificado para todos los espacios de caché -------
        /*RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .disableCachingNullValues() // No almacenar en caché los valores nulos
        .entryTtl(Duration.ofMinutes(2))
        .serializeValuesWith(RedisSerializationContext.SerializationPair
        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
        .cacheDefaults(cacheConfiguration).build();
        return cacheManager;
        */
    }

    /**
     * Configuración de RedisTemplate, métodos aplicables y sin anotación
     *
     * @param factory
     * @return
     */
   /* @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // Establecer serialización
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // Configurar redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(factory);

        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer); // serialización de claves
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // serialización de valores

        redisTemplate.setHashKeySerializer(stringSerializer); // Serialización de clave hash
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer); // serialización del valor hash
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }*/

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        // Manejo de excepciones, cuando ocurre una excepción en Redis, el registro se imprime, pero el programa se ejecuta normalmente
        log.info("Inicialización -> [{}]", "Redis CacheErrorHandler");
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }
        };
        return cacheErrorHandler;
    }
}

