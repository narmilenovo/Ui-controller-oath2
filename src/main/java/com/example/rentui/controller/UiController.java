package com.example.rentui.controller;

import com.example.cloud.commons.model.Customer;
import com.example.rentui.config.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


@Controller
@EnableOAuth2Sso
public class UiController extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated();
    }
    @Autowired
    private RestTemplate restTemplate;
    @GetMapping("/")
    public String mainUi()
    {
        return "home";
    }
    @GetMapping("/secure")
    public String securedUi()
    {
        return "secured";
    }

    @GetMapping("/findAll")
    public String getCustomers(Model model)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",AccessToken.getAccessToken());
        HttpEntity<Customer> customerHttpEntity = new HttpEntity<>(httpHeaders);
        try {
        ResponseEntity<Customer[]> responseEntity = restTemplate.exchange("http://localhost:9001/services/findAll", HttpMethod.GET,customerHttpEntity,Customer[].class);
        model.addAttribute("customers",responseEntity.getBody());
        }
        catch (HttpStatusCodeException exception)
        {
            ResponseEntity responseEntity = ResponseEntity.status(exception.getRawStatusCode()).headers(exception.getResponseHeaders()).body(exception.getResponseBodyAsString());
            model.addAttribute("error",responseEntity);
        }
        return "secured";
    }
}
