package com.rentmis.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Serves RentMIS HTML pages from classpath:/static/html/
 * Short routes redirect to their canonical HTML paths.
 */
@Controller
public class PageController {

    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity<Resource> login() {
        return serve("html/auth/login.html");
    }

    @GetMapping("/register")
    @ResponseBody
    public ResponseEntity<Resource> register() {
        return serve("html/auth/register.html");
    }

    @GetMapping("/dashboard")
    @ResponseBody
    public ResponseEntity<Resource> dashboard() {
        return serve("html/dashboard.html");
    }

    // Catch-all for /html/** — serves from classpath:/static/html/
    @GetMapping("/html/{section}/{page}")
    @ResponseBody
    public ResponseEntity<Resource> sectionPage(@PathVariable String section,
                                                 @PathVariable String page) {
        return serve("html/" + sanitize(section) + "/" + sanitize(page));
    }

    @GetMapping("/html/{page}")
    @ResponseBody
    public ResponseEntity<Resource> topPage(@PathVariable String page) {
        return serve("html/" + sanitize(page));
    }

    private String sanitize(String s) {
        return s.replaceAll("[^a-zA-Z0-9_\\-.]", "");
    }

    private ResponseEntity<Resource> serve(String path) {
        try {
            Resource resource = new ClassPathResource("static/" + path);
            if (!resource.exists()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
