package com.cg.loanemicalculator.controller;




import com.cg.loanemicalculator.dto.AuthResponseDTO;
import com.cg.loanemicalculator.dto.LoginDTO;
import com.cg.loanemicalculator.dto.RegisterDTO;
import com.cg.loanemicalculator.service.IUserService;
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
    public ResponseEntity<AuthResponseDTO<String, String>> registerUser(@RequestBody RegisterDTO registerDTO) {
        AuthResponseDTO<String, String> response = userService.registerUser(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO<String, String>> loginUser(@RequestBody LoginDTO loginDTO) {
        AuthResponseDTO<String, String> response = userService.loginUser(loginDTO);
        return ResponseEntity.ok(response);
    }






}
