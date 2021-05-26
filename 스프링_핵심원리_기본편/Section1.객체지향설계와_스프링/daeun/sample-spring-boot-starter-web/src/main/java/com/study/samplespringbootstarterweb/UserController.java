package com.study.samplespringbootstarterweb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public String get(@PathVariable Long id) {
        return new RestTemplate().getForObject("https://jsonplaceholder.typicode.com/users/{id}", String.class, id);
    }
}
