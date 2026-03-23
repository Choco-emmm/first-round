package com.dems;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DrmsW2Application {

	public static void main(String[] args) {
		// 加载 .env 文件中的环境变量
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
		SpringApplication.run(DrmsW2Application.class, args);
	}

}
