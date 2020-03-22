package com.jjunpro.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable();
//		http.authorizeRequests().antMatchers("/", "/signup", "/login", "/logout").permitAll();
//		http.authorizeRequests().antMatchers("/userInfo").access("hasRole('" + AppRole.ROLE_USER + "')");
//		http.authorizeRequests().antMatchers("/admin").access("hasRole('" + AppRole.ROLE_ADMIN + "')");
//		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
//		http.authorizeRequests().and().formLogin()
//				.loginProcessingUrl("/j_spring_security_check")
//				.loginPage("/login")
//				.defaultSuccessUrl("/userInfo")
//				.failureUrl("/login?error=true")
//				.usernameParameter("username")
//				.passwordParameter("password");
//		http.authorizeRequests().and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
//		http.apply(new SpringSocialConfigurer()).signupUrl("/signup");
	}
}