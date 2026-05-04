package com.example.smartridetrajetservice;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@FeignClient(name="smartride-user-service")
public interface UserClient {
    @RequestMapping("api/users")
    public List<User> getAllUsers();



}



