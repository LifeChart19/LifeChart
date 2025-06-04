package org.example.lifechart.domain.user.service;

import org.example.lifechart.domain.user.dto.SignupRequest;

public interface UserService {
    void signup(SignupRequest request);
}
