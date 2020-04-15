package com.jjunpro.shop.config;

import com.jjunpro.shop.service.AccountService;
import com.jjunpro.shop.service.AccountServiceImpl;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountServiceImpl accountService;
    private final PasswordEncoder    passwordEncoder;

    /* Custom userDetailsService 등록 */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder managerBuilder) throws Exception {
        managerBuilder
                .userDetailsService(this.accountService)
                .passwordEncoder(this.passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public AccessDecisionManager accessDecisionManager() {
        // roleHierarchy
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        // DefaultWebSecurityExpressionHandler
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        // setExpressionHandler
        // WebExpressionVoter 를 사용하겠습니다.
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(handler);

        // AccessDecisionVoter
        // Voter 목록을 만듭니다.
        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);

        return new AffirmativeBased(voters);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* 접근권한 */
        http
                .authorizeRequests()
                .antMatchers("/", "/logout")
                .permitAll();

        http
                .authorizeRequests()
                .antMatchers("/join", "/login")
                .anonymous();

        /* ADMIN / USER 권한 상하관계 설정 */
        http
                .authorizeRequests()
                .antMatchers("/order/form", "/order/receipt/**")
                .authenticated()
                .accessDecisionManager(accessDecisionManager());

        http
                .authorizeRequests()
                .antMatchers("/admin/**", "/shopgroup/**", "/product/**")
                .hasRole("ADMIN");

        /* 잘못된 접근인경우 "/" 경로로 이동 */
        http
                .authorizeRequests().and()
                .exceptionHandling().accessDeniedPage("/");

        /* Login */
        http
                .authorizeRequests().and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/");

        /**
         *  Logout
         *
         *  invalidateHttpSession() - 로그아웃 성공시 인증정보를 지우하고 세션을 무효화
         */
        http
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies();
    }
}