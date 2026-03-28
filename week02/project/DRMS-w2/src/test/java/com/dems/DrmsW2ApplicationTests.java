package com.dems;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class DrmsW2ApplicationTests {

	static {
		// 强制加载项目根目录下的 .env 文件到系统属性
		io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
				.directory("./") // 指定根目录
				.ignoreIfMissing()
				.load();
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
	}
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	void contextLoads() {

		stringRedisTemplate.opsForValue().set("name", "dems");
		System.out.println(stringRedisTemplate.opsForValue().get("name"));
	}

}
