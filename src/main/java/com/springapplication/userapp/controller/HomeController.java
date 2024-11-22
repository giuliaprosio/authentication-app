package com.springapplication.userapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class HomeController {

    @GetMapping("/api/home")
    @ResponseBody
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("home", HttpStatus.OK);
    }


}
