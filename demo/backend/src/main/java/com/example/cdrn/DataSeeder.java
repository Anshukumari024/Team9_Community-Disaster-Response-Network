package com.example.cdrn;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.cdrn.model.AppUser;
import com.example.cdrn.model.Incident;
import com.example.cdrn.repository.IncidentRepository;
import com.example.cdrn.repository.UserRepository;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(IncidentRepository ir, UserRepository ur) {
        return args -> {
            if (ur.count() == 0) {
                var u1 = new AppUser(); u1.setUsername("alice"); u1.setDisplayName("Alice"); u1.setRole("CITIZEN"); u1.setVerified(true);
                var u2 = new AppUser(); u2.setUsername("bob"); u2.setDisplayName("Bob"); u2.setRole("VOLUNTEER"); u2.setVerified(true);
                ur.save(u1); ur.save(u2);
            }

            if (ir.count() == 0) {
                var i1 = new Incident(); i1.setType("Flood"); i1.setDescription("Water level rising near riverbank"); i1.setLatitude(12.9716); i1.setLongitude(77.5946); i1.setSeverity("HIGH"); i1.setReporterName("Alice"); i1.setContact("+911234567890"); i1.setStatus("REPORTED");
                var i2 = new Incident(); i2.setType("Blocked Road"); i2.setDescription("Tree fallen blocking main road"); i2.setLatitude(12.9721); i2.setLongitude(77.5960); i2.setSeverity("MEDIUM"); i2.setReporterName("Bob"); i2.setContact("+911098765432"); i2.setStatus("REPORTED");
                ir.save(i1); ir.save(i2);
            }
        };
    }
}
