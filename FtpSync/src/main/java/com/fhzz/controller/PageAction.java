package com.fhzz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageAction {
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	@GetMapping("/index")
	public String indexPage() {
		return "index";
	}
	
	@GetMapping("/ftpFileLink")
	public String ftpFileLinkPage() {
		return "ftpFileLink";
	}
	
}
