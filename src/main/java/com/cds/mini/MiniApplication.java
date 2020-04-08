package com.cds.mini;

import com.cds.mini.entity.Account;
import com.cds.mini.entity.User;
import com.cds.mini.repository.AccountRepository;
import com.cds.mini.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootApplication
public class MiniApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniApplication.class, args);
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return (args) -> {
			User user = userRepository.findByUserId("0001").orElse(null);
			if (user != null) {
				IntStream.rangeClosed(1, 10).mapToObj(index -> {
					Account account = new Account();
					account.setUser(user);
					account.setAccountNumber("00000" + index);

					return account;
				}).forEach(account -> accountRepository.save(account));
			}

			accountRepository.findAll().forEach(account -> System.out.println(account.getAccountNumber()));
		};
	}
}
