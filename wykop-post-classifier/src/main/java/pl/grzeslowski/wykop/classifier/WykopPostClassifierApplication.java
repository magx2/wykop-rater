package pl.grzeslowski.wykop.classifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = WykopPostClassifierApplication.class)
@SpringBootApplication
public class WykopPostClassifierApplication {

	public static void main(String[] args) {
		SpringApplication.run(WykopPostClassifierApplication.class, args);
	}
}
