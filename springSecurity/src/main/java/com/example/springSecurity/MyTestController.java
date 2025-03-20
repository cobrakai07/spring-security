package com.example.springSecurity;

import com.example.springSecurity.dto.LoginRequest;
import com.example.springSecurity.dto.LoginResponse;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

}
