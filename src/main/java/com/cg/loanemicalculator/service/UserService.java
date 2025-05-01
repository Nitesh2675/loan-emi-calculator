
package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.AuthResponseDTO;
import com.cg.loanemicalculator.dto.LoginDTO;
import com.cg.loanemicalculator.dto.RegisterDTO;
import com.cg.loanemicalculator.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.cg.loanemicalculator.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements IUserService {



    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;


    @Override
    public AuthResponseDTO<String, String> registerUser(RegisterDTO registerDTO) {
        log.info("Registering user:{}", registerDTO.getEmail());
        AuthResponseDTO<String, String> res = new AuthResponseDTO<>();

        // Check if the user already exists by email
        if (existsByEmail(registerDTO.getEmail())) {
            log.warn("Registration failed: user already exists with email {}", registerDTO.getEmail());
            res.setMessage("error");
            res.setMessageData("User already exists with this email");
            return res;
        }

        // Creating a new User object from RegisterDTO
        User user = new User();
        user.setUsername(registerDTO.getFullName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password

        userRepository.save(user); // Save user to the database
        log.info("User {} registered successfully", user.getEmail());

        // Send registration confirmation email
        emailService.sendEmail(user.getEmail(), "Registered", "Hi " + user.getUsername() + ",\n\nYou have been successfully registered in the Employee Payroll App.");

        // Return response with success message
        res.setMessage("message");
        res.setMessageData("User registered successfully");
        return res;
    }


    @Override
    public AuthResponseDTO<String, String> loginUser(LoginDTO loginDTO) {
        return null;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public AuthResponseDTO<String, String> forgotPassword(String email) {
        return null;
    }

    @Override
    public AuthResponseDTO<String, String> resetPassword(String email, String otp, String newPassword) {
        return null;
    }
}
