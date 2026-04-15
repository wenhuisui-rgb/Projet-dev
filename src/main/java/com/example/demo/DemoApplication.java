package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.repository.BadgeRepository;

@SpringBootApplication
public class DemoApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/badges").setViewName("forward:/badges.html");
	}

	@Bean
	public CommandLineRunner initBadges(BadgeRepository repository) {
		return args -> {

			// Création directe des 5 badges
			repository.save(new Badge("Marathonien", "Courir un marathon (42.195 km)", TypeSport.COURSE, 42.195f));
			repository.save(new Badge("Nageur 1K", "Nager 1		 km", TypeSport.NATATION, 1f));
			repository.save(new Badge("Randonneur 20K", "Faire une randonnée de 20 km", TypeSport.RANDONNEE, 20f));
			repository.save(new Badge("Cycliste 100K", "devloper coucher 100 kg", TypeSport.MUSCULATION, 100f));
			repository.save(new Badge("Triathlete", "Combiner 1 km de natation, 20 km de vélo et 5 km de course",
					TypeSport.COURSE, 5f)); // Simplifié pour l'exemple

		};
	}

}
