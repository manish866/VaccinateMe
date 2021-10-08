package com.example.cowin.web;

import com.example.cowin.schedular.VaccineNotifierScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

@Controller
public class AppInventoryRestApiController {


	@Autowired
	private VaccineNotifierScheduler vaccineNotifierScheduler;

	@GetMapping("")
	public ResponseEntity<HttpStatus> getStatus() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/result")
	public ResponseEntity<List<String>> getResult() {
		List<String> vaccineSlotsAvailability = null;
		try {
			vaccineSlotsAvailability = vaccineNotifierScheduler.findVaccineSlotsAvailability();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(vaccineSlotsAvailability, HttpStatus.OK);
	}

	@GetMapping("/pincode/{pincode}/date/{date}")
	public ResponseEntity<List<String>> getResult(@PathVariable String pincode, @PathVariable String date) {
		List<String> vaccineSlotsAvailability = null;
		try {
			vaccineSlotsAvailability = vaccineNotifierScheduler.findVaccineSlotsAvailabilityWithPinAndDate(pincode, date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(vaccineSlotsAvailability, HttpStatus.OK);
	}

}
