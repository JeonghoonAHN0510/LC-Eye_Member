package lceye.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lceye.service.JwtService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;
} // class end