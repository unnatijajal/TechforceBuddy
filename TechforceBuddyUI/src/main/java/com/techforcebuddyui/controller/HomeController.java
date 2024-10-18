package com.techforcebuddyui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@GetMapping("/search")
	public String searchPage() {
		return "searchPage";
	}
	
	@GetMapping("/pdfViewer")
    public String pdfViewer(@RequestParam("file") String fileName, Model model) {
        // Add the file name as an attribute to the model
        model.addAttribute("fileName", fileName);
        // Return the view name
        return "pdfViewer";
    }
}
