package ca.on.pms.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

	@GetMapping("/echo")
	public ResponseEntity<Map<String, String>> echo() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Yes, I am online");
		response.put("status", "UP");
		response.put("timestamp", LocalDateTime.now().toString());

		return ResponseEntity.ok(response);
	}
}