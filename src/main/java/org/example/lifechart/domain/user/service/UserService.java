package org.example.lifechart.domain.user.service;

import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.entity.User;

public interface UserService {
    User signup(SignupRequest request);
}
