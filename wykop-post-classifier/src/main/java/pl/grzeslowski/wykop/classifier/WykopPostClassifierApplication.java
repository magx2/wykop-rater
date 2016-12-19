package pl.grzeslowski.wykop.classifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import pl.grzeslowski.wykop.classifier.test.Trainer;

@ComponentScan(basePackageClasses = WykopPostClassifierApplication.class)
@SpringBootApplication
public class WykopPostClassifierApplication implements CommandLineRunner {

    private final Trainer trainer;

    @Autowired
    public WykopPostClassifierApplication(Trainer trainer) {
        this.trainer = trainer;
    }

    public static void main(String[] args) {
        SpringApplication.run(WykopPostClassifierApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        trainer.trainAndTest();
    }
}
