package ca.on.pms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
// ✅ NEW IMPORT for Spring Boot 3.4+ / 4.0+
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;

import ca.on.pms.notification.service.EmailService;

@SpringBootTest
class PmsApiApplicationTests {

	// ✅ Replace @MockBean with @MockitoBean
	@MockitoBean
	private JavaMailSender javaMailSender;

	@MockitoBean
	private EmailService emailService;

	@Test
	void contextLoads() {
		// Application context should load successfully with mocks
	}

}