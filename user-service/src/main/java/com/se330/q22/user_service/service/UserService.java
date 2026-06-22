package com.se330.q22.user_service.service;

import com.se330.q22.user_service.entity.User;
import com.se330.q22.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository userRepository;

    public List<User> getAll()
    {
        return userRepository.findAll();
    }
}
