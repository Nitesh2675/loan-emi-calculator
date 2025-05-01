package com.cg.loanemicalculator.util;



import com.cg.loanemicalculator.model.User;
import com.cg.loanemicalculator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user=userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found with email"+email));
        return user;
    }
}

