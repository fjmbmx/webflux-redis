package com.santander;

import com.santander.model.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class MainFlux {

    public static void main(String[] args) throws InterruptedException {
        Mono mono1 = Mono.just("ozenero.com");
        Mono mono2 = Mono.just("|Java Technology");
        Mono mono3 = Mono.just("|Spring Framework");

        System.out.println("=== Flux.concat(mono1, mono3, mono2) ===");
        Flux.concat(mono1, mono3, mono2).subscribe(System.out::print);

        System.out.println("\n=== combine the value of mono1 then mono2 then mono3 ===");
        mono1.concatWith(mono2).concatWith(mono3).subscribe(System.out::print);

        Flux flux1 = Flux.just("{1}", "{2}", "{3}", "{4}");
        Flux flux2 = Flux.just("|A|", "|B|", "|C|");

        System.out.println("\n=== Flux.zip(flux2, flux1, combination) ===");
        Flux.zip(flux2, flux1,
                (itemFlux2, itemFlux1) -> "-[" + itemFlux2 + itemFlux1 + "]-")
                .subscribe(System.out::print);

        System.out.println("\n=== flux1 values zip with flux2 values ===");
        flux1.zipWith(flux2,
                (itemFlux1, itemFlux2) -> "-[" + itemFlux1 + itemFlux2 + "]-")
                .subscribe(System.out::print);

        Flux intervalFlux1 = Flux
                .interval(Duration.ofMillis(500))
                .zipWith(flux1, (i, string) -> string);

        Flux intervalFlux2 = Flux
                .interval(Duration.ofMillis(700))
                .zipWith(flux2, (i, string) -> string);

        System.out.println("\n=== Flux.concat(flux2, flux1) ===");
        Flux.concat(flux2, flux1).subscribe(System.out::print);

        System.out.println("\n=== flux1 values and then flux2 values ===");
        flux1.concatWith(flux2).subscribe(System.out::print);

        System.out.println("\n=== Flux.concat(intervalFlux2, flux1) ===");
        Flux.concat(intervalFlux2, flux1).subscribe(System.out::print);
        Thread.sleep(3000);

        System.out.println("\n=== intervalFlux1 values and then flux2 values ===");
        intervalFlux1.concatWith(flux2).subscribe(System.out::print);
        Thread.sleep(3000);

        System.out.println("\n=== Flux.concat(intervalFlux2, intervalFlux1) ===");
        Flux.concat(intervalFlux2, intervalFlux1).subscribe(System.out::print);
        Thread.sleep(5000);

        System.out.println("\n=== intervalFlux1 values and then intervalFlux2 values ===");
        intervalFlux1.concatWith(intervalFlux2).subscribe(System.out::print);
        Thread.sleep(5000);

        System.out.println("\n=== Flux.merge(intervalFlux1, intervalFlux2) ===");
        Flux.merge(intervalFlux1, intervalFlux2).subscribe(System.out::print);
        Thread.sleep(3000);

        System.out.println("\n=== interleave flux1 values with flux2 values ===");
        intervalFlux1.mergeWith(intervalFlux2).subscribe(System.out::print);
        Thread.sleep(3000);


        Flux<Profile> flux = Flux.range(0, 10)
                .map(o -> new Profile())
                .map(profile -> profile);

        Mono<Mono<String>> var = Mono.fromCallable(() -> Mono.just("xxx")).map(s -> s);
    }

}
