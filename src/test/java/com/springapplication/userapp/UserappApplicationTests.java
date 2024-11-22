package com.springapplication.userapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:mysql://localhost:3306/mydb",  // Or your preferred DB URL
		"spring.datasource.username=root",
		"spring.datasource.password=root",
		"spring.datasource.platform=mysql",
		"spring.sql.init.mode=never", // Disable SQL initialization (no connection made)
		"spring.jpa.hibernate.ddl-auto=none"  // Don't perform schema creation
})
class UserappApplicationTests {

	@Test
	void contextLoads() {
	}

}
