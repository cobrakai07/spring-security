package com.example.springSecurity.controller;

import com.example.springSecurity.dto.LoginRequest;
import com.example.springSecurity.dto.LoginResponse;
import com.example.springSecurity.dto.RegistrationRequest;
import com.example.springSecurity.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class MyTestController {

    @Autowired
    private JwtUtils utils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/test")
    public String fun(){
        return "Testing endpoint...!";
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String user(){
        return "Testing User endpoint...!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin(){
        return "Testing Admin endpoint...!";
    }



    // jwt endpoints
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        }catch(AuthenticationException ex){
            Map<String, Object> map= new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status",false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();

        String jwtToken = utils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item-> item.getAuthority()).collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getUsername(),roles, jwtToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody RegistrationRequest user) {
        JdbcUserDetailsManager userDetailsManager=
                new JdbcUserDetailsManager(dataSource);
        if(userDetailsManager.userExists(user.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!");
        }
        UserDetails newUser = User.withUsername(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(user.getRole())
                .build();
        userDetailsManager.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!");
    }

}
