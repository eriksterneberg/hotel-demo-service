package com.hotel.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires Cassandra - enable for integration tests")
class HotelDemoServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
