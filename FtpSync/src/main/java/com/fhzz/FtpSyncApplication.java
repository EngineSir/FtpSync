package com.fhzz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan //过滤器配置
public class FtpSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtpSyncApplication.class, args);
	}

}
