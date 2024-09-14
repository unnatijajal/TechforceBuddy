package com.techforcebuddyui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String index() {
		return "login";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/home")
	public String home() {
		return "home";
	}
	@GetMapping("/welcome")
	public String welcome() {
		return "welcome";
	}
	@GetMapping("/chatbot")
	public String chatbot() {
		return "chatbot";
	}
}
