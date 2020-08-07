package com.example.spring.security;

import java.security.Principal;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringSecurityApplication {
	
	@Bean
	UserDetailsManager memory(DataSource ds) {
			
		//1. In Memory Authentication
		//return new InMemoryUserDetailsManager();
		
		//2. JdbcBased Authentication
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		jdbcUserDetailsManager.setDataSource(ds);
		return jdbcUserDetailsManager;
	
	}

	@Bean
	InitializingBean initializer(UserDetailsManager manager) {
			return () -> {

					UserDetails josh = User.withDefaultPasswordEncoder().username("akash").password("password").roles("USER").build();
					manager.createUser(josh);

					UserDetails rob = User.withUserDetails(josh).username("sagar").roles("INVALID").build();
					manager.createUser(rob);
			};
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}

}

@RestController
class GreetingsRestController {

		@GetMapping("/greeting")
		String greeting(Principal principal) {
				return "hello, " + principal.getName() + "!";
		}
}

@Configuration
@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	// FilterChainProxy is main entry class for Spring Security

		@Override
		protected void configure(HttpSecurity http) throws Exception {

				/*http.httpBasic();
				http.authorizeRequests().anyRequest().hasRole("USER");*/
				
				http.httpBasic().and().authorizeRequests().anyRequest().hasRole("USER");
				
				http
	                .authorizeRequests().antMatchers("/console/**").permitAll();
				
				http.csrf().disable();
				http.headers().frameOptions().disable();
		}
}

