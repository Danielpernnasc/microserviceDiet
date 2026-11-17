package com.trainday.microserviceDiet;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class MicroserviceDietApplicationTest {

	@Test
	void mainDelegatesToSpringApplicationRun() {
		String[] args = {"--spring.profiles.active=test"};

		try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
			MicroserviceDietApplication.main(args);

			springApplication.verify(() -> SpringApplication.run(MicroserviceDietApplication.class, args));
		}
	}
}
