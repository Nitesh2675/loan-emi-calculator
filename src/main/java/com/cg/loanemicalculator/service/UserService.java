
package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.AuthResponseDTO;
import com.cg.loanemicalculator.dto.LoginDTO;
import com.cg.loanemicalculator.dto.RegisterDTO;
import com.cg.loanemicalculator.model.User;
import com.cg.loanemicalculator.util.JwtUtility;
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

    @Autowired
    private JwtUtility jwtUtility;


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
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password

        userRepository.save(user); // Save user to the database
        log.info("User {} registered successfully", user.getEmail());

        // Send registration confirmation email
        //emailService.sendEmail(user.getEmail(), "Registered", "Hi " + user.getUsername() + ",\n\nYou have been successfully registered in the Employee Payroll App.");

        // Return response with success message
        //res.setMessage("message");
        res.setMessageData("User registered successfully");
        return res;
    }


    @Override
    public AuthResponseDTO<String, String> loginUser(LoginDTO loginDTO) {
        log.info("Login attempt for user: {}", loginDTO.getEmail());
        AuthResponseDTO<String, String> res = new AuthResponseDTO<>();
        Optional<User> userExists = getUserByEmail(loginDTO.getEmail()); // Fetch user by email

        // Check if user exists
        if (userExists.isPresent()) {
            User user = userExists.get();

            // Validate password
            if (matchPassword(loginDTO.getPassword(), user.getPassword())) {
                String token = jwtUtility.generateToken(user.getEmail()); // Generate JWT token

                log.debug("Login successful for user: {} - Token generated", user.getEmail());

                // Send login confirmation email with token
                //emailService.sendEmail(user.getEmail(), "Logged into Employee Payroll App", "Hi " + user.getUsername() + ",\n\nYou have been successfully logged in! Your token is: " + token);

                // Return response with success message and token
                //res.setMessage("message");
                res.setMessageData("Login successful");
                res.setToken(token); // Adding token in the response DTO
                return res;
            } else {
                // If password is incorrect
                log.warn("Invalid credentials for user: {}", loginDTO.getEmail());
                res.setMessage("error");
                res.setMessageData("Invalid credentials. Please check your email and password.");
                return res;
            }
        }

        // If user does not exist
        log.error("User not found with email: {}", loginDTO.getEmail());
        res.setMessage("error");
        res.setMessageData("User not found with this email.");
        return res;
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


    public boolean matchPassword(String rawPassword, String encodedPassword) {
        log.debug("Matching password for login attempt");
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
