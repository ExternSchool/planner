package io.github.externschool.planner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/index", "/home", "/greeting",
                        "/h2-console/**", //if being logged out we need to reach H2 console
                        "/css/**",
                        "/img/**").permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .defaultSuccessUrl("/", true)
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authBuilder) throws Exception{
        authBuilder.inMemoryAuthentication()
                .withUser("user@a").password("{noop}user").roles("USER")
                .and()
                .withUser("admin@a").password("{noop}admin").roles("USER", "ADMIN");
    }
}
