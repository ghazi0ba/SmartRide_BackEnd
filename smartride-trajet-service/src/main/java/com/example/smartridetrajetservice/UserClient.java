package com.example.smartridetrajetservice;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@FeignClient(name="smartride-user-service")
public interface UserClient {
    @GetMapping("api/users")
    public List<User> getAllUsers();

    @GetMapping("api/users/{id}")
    public User getUserById(@PathVariable Long id);





}



