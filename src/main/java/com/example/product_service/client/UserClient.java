package com.example.product_service.client;

import com.example.product_service.dto.NotificationRequest;
import com.example.product_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/api/auth/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request);

    @GetMapping("/api/auth/user/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
