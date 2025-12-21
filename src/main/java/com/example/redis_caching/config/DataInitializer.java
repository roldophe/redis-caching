package com.example.redis_caching.config;

import com.example.redis_caching.entity.User;
import com.example.redis_caching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;

  @Override
  public void run(String... args) {
    if (userRepository.count() == 0) {
      log.info("Initializing database with sample users...");

      User user1 = new User(null, "John Doe", "john.doe@example.com", "+1-555-0101");
      User user2 = new User(null, "Jane Smith", "jane.smith@example.com", "+1-555-0102");
      User user3 = new User(null, "Bob Johnson", "bob.johnson@example.com", "+1-555-0103");
      User user4 = new User(null, "Alice Williams", "alice.williams@example.com", "+1-555-0104");
      User user5 = new User(null, "Charlie Brown", "charlie.brown@example.com", "+1-555-0105");

      userRepository.save(user1);
      userRepository.save(user2);
      userRepository.save(user3);
      userRepository.save(user4);
      userRepository.save(user5);

      log.info("Successfully initialized 5 users in the database");
    } else {
      log.info(
          "Database already contains {} users, skipping initialization", userRepository.count());
    }
  }
}
