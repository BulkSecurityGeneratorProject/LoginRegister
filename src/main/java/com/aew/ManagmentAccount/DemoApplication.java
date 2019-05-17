package com.aew.ManagmentAccount;

import java.util.HashSet;
import java.util.Set;

import com.aew.ManagmentAccount.domain.Authority;
import com.aew.ManagmentAccount.domain.RoleName;
import com.aew.ManagmentAccount.domain.User;
import com.aew.ManagmentAccount.repository.AuthorityRepository;
import com.aew.ManagmentAccount.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class DemoApplication implements ApplicationRunner {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {

		authorityRepository.save(new Authority(RoleName.ROLE_MOD));
		authorityRepository.save(new Authority(RoleName.ROLE_ADMIN));
		authorityRepository.save(new Authority(RoleName.ROLE_USER));
		authorityRepository.save(new Authority(RoleName.ROLE_ANONYMOUS));
		Set<Authority> roles = new HashSet<>();
		roles.add(new Authority(RoleName.ROLE_ADMIN));
		User admin = User.builder().login("admin").email("adrianwilgenhof@gmail.com").password(encoder.encode("123456"))
				.firstName("adrian").lastName("wilgenhoff").activated(true).authorities(roles).build();
		userRepository.save(admin);
		logger.info("Creation of admin user");
	}
}
