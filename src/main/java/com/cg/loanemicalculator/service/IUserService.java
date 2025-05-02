package com.cg.loanemicalculator.service;



import com.cg.loanemicalculator.dto.AuthResponseDTO;
import com.cg.loanemicalculator.dto.LoginDTO;
import com.cg.loanemicalculator.dto.RegisterDTO;
import com.cg.loanemicalculator.model.User;

import java.util.Optional;

public interface IUserService {


    AuthResponseDTO<String ,String> registerUser(RegisterDTO registerDTO);


    AuthResponseDTO<String, String> loginUser(LoginDTO loginDTO);


    Optional<User> getUserByEmail(String email);

    AuthResponseDTO<String, String> forgotPassword(String email);

    AuthResponseDTO<String, String> resetPassword(String email, String otp, String newPassword);

}

