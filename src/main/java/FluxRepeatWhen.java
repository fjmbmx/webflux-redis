import com.santander.model.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FluxRepeatWhen {

    public static void main(String[] args) throws InterruptedException {

        callAPI2();
        Thread.sleep(90000);
    }

    private static Mono<List<Profile>> extracted() throws InterruptedException {
        Profile profile = new Profile();
        profile.setId("1");
        profile.setName("Admin");

        Profile profile2 = new Profile();
        profile2.setId("2");
        profile2.setName("User");
        return callAPI("3", "Pagina").expand(res -> {
            int random = (int) (Math.random() * 20);
            //System.out.println(random);
            // System.out.println(res);

            if (!res.getId().equals("5")) {
                return callAPI(String.valueOf(random), "Pagina");
            }
            return Mono.empty();
        }).collectList();
    }

    private static void callAPI2() {

        callAPI("3", "Pagina")
                .flatMap(p -> {
                    int random = (int) (Math.random() * 20);
                    return callAPI(String.valueOf(random), "Pagina");
                })
                .repeat()
                .takeWhile(profile -> profile.getId().equals("10"))
                .log()
                .subscribe(profile -> System.out.println(profile));
    }

    private static Mono<Profile> callAPI(String values, String pagina) {
        Profile profile = new Profile();
        profile.setId(values);
        profile.setName(pagina + values);
        return Mono.just(profile).delayElement(Duration.ofSeconds(1));
    }
}
