package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.repository.BadgeRepository;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

	/* 
		ajouter badges - ne toucher pas !!!!!!!!!!!!!!!!!!!! 
	*/
    @Bean
    public CommandLineRunner initBadges(BadgeRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                System.out.println("Badges already exist, skipping initialization.");
                return;
            }
            
            repository.save(new Badge("PREMIER_PAS", "Première activité", null, 1));
            repository.save(new Badge("SPORTIF_10", "10 activités", null, 10));
            repository.save(new Badge("SPORTIF_50", "50 activités", null, 50));
            repository.save(new Badge("TOTAL_100KM", "100 km au total", null, 100));
            repository.save(new Badge("TOTAL_500KM", "500 km au total", null, 500));
            repository.save(new Badge("TOTAL_1000MIN", "1000 minutes de sport", null, 1000));
            repository.save(new Badge("TOTAL_5000MIN", "5000 minutes de sport", null, 5000));
            
            repository.save(new Badge("COURSE_5KM", "5 km en une course", TypeSport.COURSE, 5));
            repository.save(new Badge("COURSE_10KM", "10 km en une course", TypeSport.COURSE, 10));
            repository.save(new Badge("COURSE_SEMI", "Semi-marathon (21.1 km)", TypeSport.COURSE, 21.1f));
            repository.save(new Badge("COURSE_MARATHON", "Marathon (42.2 km)", TypeSport.COURSE, 42.2f));
            repository.save(new Badge("COURSE_100KM", "100 km cumulés", TypeSport.COURSE, 100));
            
            repository.save(new Badge("NATATION_1KM", "1 km en une séance", TypeSport.NATATION, 1));
            repository.save(new Badge("NATATION_2KM", "2 km en une séance", TypeSport.NATATION, 2));
            repository.save(new Badge("NATATION_5KM", "5 km en une séance", TypeSport.NATATION, 5));
            repository.save(new Badge("NATATION_10KM", "10 km en une séance", TypeSport.NATATION, 10));
            
            repository.save(new Badge("VELO_50KM", "50 km en une sortie", TypeSport.VELO, 50));
            repository.save(new Badge("VELO_100KM", "100 km en une sortie", TypeSport.VELO, 100));
            repository.save(new Badge("VELO_200KM", "200 km en une sortie", TypeSport.VELO, 200));
            
            repository.save(new Badge("MUSCULATION_10", "10 séances", TypeSport.MUSCULATION, 10));
            repository.save(new Badge("MUSCULATION_50", "50 séances", TypeSport.MUSCULATION, 50));
            
            repository.save(new Badge("YOGA_10", "10 séances de yoga", TypeSport.YOGA, 10));
            repository.save(new Badge("YOGA_50", "50 séances de yoga", TypeSport.YOGA, 50));
            
            repository.save(new Badge("RANDONNEE_10KM", "10 km en randonnée", TypeSport.RANDONNEE, 10));
            repository.save(new Badge("RANDONNEE_20KM", "20 km en randonnée", TypeSport.RANDONNEE, 20));
            
            System.out.println("20 badges initialized successfully!");
        };
    }
}