package ro.unitbv.restlab.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ro.unitbv.restlab.model.AppUser;
import ro.unitbv.restlab.model.Category;
import ro.unitbv.restlab.repository.AppUserRepository;
import ro.unitbv.restlab.repository.CategoryRepository;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .roles(Set.of("ADMIN"))
                    .build();

            AppUser user = AppUser.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .enabled(true)
                    .roles(Set.of("USER"))
                    .build();

            AppUser manager = AppUser.builder()
                    .username("manager")
                    .password(passwordEncoder.encode("manager123"))
                    .enabled(true)
                    .roles(Set.of("MANAGER"))
                    .build();

            userRepository.saveAll(Set.of(admin, user, manager));
            System.out.println("Baza de date a fost populată cu utilizatori de test.");
        }

        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    new Category(null, "IT"),
                    new Category(null, "Office"),
                    new Category(null, "Home"),
                    new Category(null, "Books")
            ));
            System.out.println("Baza de date a fost populată cu categorii de test.");
        }
    }
}
