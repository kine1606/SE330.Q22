package com.se330.q22.user_service;

import com.se330.q22.user_service.entity.Role;
import com.se330.q22.user_service.entity.User;
import com.se330.q22.user_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.saveAll(List.of(
                    User.builder()
                            .email("test@gmail.com")
                            .password(passwordEncoder.encode("123"))
                            .fullName("Khách hàng Mẫu")
                            .role(Role.USER)
                            .build()
            ));
            System.out.println("✅ Đã tạo tài khoản mẫu: test@gmail.com / mật khẩu: 123");
        }
    }
}
