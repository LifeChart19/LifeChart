package org.example.lifechart.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok().build(); // 또는 id 반환 등
    }
}
