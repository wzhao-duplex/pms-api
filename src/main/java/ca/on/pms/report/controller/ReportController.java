package ca.on.pms.report.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.on.pms.report.dto.T776ReportLineDto;
import ca.on.pms.report.service.TaxReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final TaxReportService reportService;

	@GetMapping("/t776")
	public ResponseEntity<List<T776ReportLineDto>> getT776Report(@RequestParam UUID propertyId,
			@RequestParam int year) {
		return ResponseEntity.ok(reportService.generateT776Report(propertyId, year));
	}
}