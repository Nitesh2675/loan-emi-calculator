package com.cg.loanemicalculator.controller;




import com.cg.loanemicalculator.dto.AuthResponseDTO;
import com.cg.loanemicalculator.dto.LoginDTO;
import com.cg.loanemicalculator.dto.RegisterDTO;
import com.cg.loanemicalculator.dto.ResetPasswordDTO;
import com.cg.loanemicalculator.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // Matches "/auth/register" and "/auth/login" from security config
class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO<String, String>> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        AuthResponseDTO<String, String> response = userService.registerUser(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO<String, String>> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO<String, String> response = userService.loginUser(loginDTO);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponseDTO<String, String>> forgotPassword(@RequestBody String email) {
        AuthResponseDTO<String, String> response = userService.forgotPassword(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponseDTO<String, String>> resetPassword(
           @Valid @RequestBody ResetPasswordDTO dto) {

        AuthResponseDTO<String, String> response = userService.resetPassword(dto.getEmail(), dto.getOtp(), dto.getNewPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }









}
