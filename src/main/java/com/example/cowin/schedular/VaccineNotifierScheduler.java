package com.example.cowin.schedular;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.cowin.service.SendWhatsAppMessageService;
import com.example.cowin.service.WebClientService;

@Component
public class VaccineNotifierScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(VaccineNotifierScheduler.class);
	private static final String[] PINCODELIST = { "411028" };
	private static final String DOSE_NUMBER = "2";

	private final SendWhatsAppMessageService sendWhatsAppMessageService;
	private final WebClientService webClientService;

	private String schedulerZoneId = "Asia/Kolkata";

	VaccineNotifierScheduler(SendWhatsAppMessageService sendWhatsAppMessageService, WebClientService webClientService) {
		this.sendWhatsAppMessageService = sendWhatsAppMessageService;
		this.webClientService = webClientService;
	}

	// This will hit covin at an interval of 10 minutes
	//@Scheduled(cron = "0 */10 * * * ?", zone = "Asia/Kolkata")
	public List<String> findVaccineSlotsAvailability() throws IOException {
		List<String> response;
		List<String> message = new ArrayList<>();
		try{
			LOGGER.info("************* findVaccineSlotsAvailability has been started............... *************");
			for (String pin : PINCODELIST) {
				String formattedDate = getCurrentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				response = webClientService.get(pin, formattedDate);
				for (String res : response) {
					message.addAll(prepareMessageDraft(res));
				}
				sendWhatsAppMessageService.sendMessage(message);
			}
		}catch (Exception e){

		}
		return message;
	}

	public List<String> findVaccineSlotsAvailabilityWithPinAndDate(String pincode, String date) throws IOException {
		List<String> response;
		List<String> message = new ArrayList<>();
		try{
			LOGGER.info("************* findVaccineSlotsAvailabilityWithPinAndDate has been started............... *************");
			response = webClientService.get(pincode, date);
			for (String res : response) {
				message.addAll(prepareMessageDraft(res));
			}
			sendWhatsAppMessageService.sendMessage(message);
		}catch (Exception e){
			e.printStackTrace();
		}
		return message;
	}

	JSONArray prepareMessage(String response) {
		List<String> messages = new ArrayList<>();
		JSONArray toBeReturned = new JSONArray();
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray centers = (JSONArray) jsonObject.get("centers");
			(centers).forEach(center -> {
				JSONObject availableCenter = (JSONObject) center;
				JSONArray sessions = (JSONArray) (availableCenter).get("sessions");
				sessions.forEach(session -> {
					JSONObject givenSession = (JSONObject) session;
					int availableCapacity = ((int) givenSession.get("available_capacity_dose" + DOSE_NUMBER));
					if (availableCapacity > 0) {
						JSONObject result = new JSONObject();
						result.put("name" , availableCenter.get("name"));
						result.put("pincode", availableCenter.get("pincode"));
						result.put("date", givenSession.getString("date"));
						result.put("availableCapacity", availableCapacity);

						result.put("vaccine", givenSession.getString("vaccine"));
						result.put("minimum age limit", givenSession.get("min_age_limit"));
						toBeReturned.put(result);
						//messages.add(message);
					}
				});
			});
		} catch (JSONException err) {
			LOGGER.error(err.toString());
		}
		return toBeReturned;
	}

	List<String> prepareMessageDraft(String response) {
		List<String> messages = new ArrayList<>();
		try {
			String head = "\uD835\uDE71\uD835\uDE7C\uD835\uDE82\uD835\uDE78\uD835\uDE83 \uD835\uDE7C\uD835\uDE72\uD835\uDE70 \uD835\uDE73\uD835\uDE74\uD835\uDE7F\uD835\uDE70\uD835\uDE81\uD835\uDE83\uD835\uDE7C\uD835\uDE74\uD835\uDE7D\uD835\uDE83 \uD835\uDE72\uD835\uDE7E\uD835\uDE85\uD835\uDE78\uD835\uDE73 \uD835\uDE82\uD835\uDE7B\uD835\uDE7E\uD835\uDE83\uD835\uDE82 \uD835\uDE70\uD835\uDE85\uD835\uDE70\uD835\uDE78\uD835\uDE7B\uD835\uDE70\uD835\uDE71\uD835\uDE7B\uD835\uDE78\uD835\uDE83\uD835\uDE88 ] \n";
			messages.add(head);
			JSONObject jsonObject = new JSONObject(response);
			JSONArray centers = (JSONArray) jsonObject.get("centers");
			(centers).forEach(center -> {
				JSONObject availableCenter = (JSONObject) center;
				JSONArray sessions = (JSONArray) (availableCenter).get("sessions");
				sessions.forEach(session -> {
					JSONObject givenSession = (JSONObject) session;
					int availableCapacity = ((int) givenSession.get("available_capacity_dose" + DOSE_NUMBER));
					if (availableCapacity > 0) {
						String message = "Place:" + availableCenter.get("name") + "\n" + "Pincode:"
								+ availableCenter.get("pincode") + "\n" + "date:" + givenSession.getString("date") + "\n"
								+ "AvailableCapacity:" + availableCapacity + "\n" + "Vaccine:"
								+ givenSession.getString("vaccine") + "\n" + "Age:" + givenSession.get("min_age_limit") +"\n\n";
						messages.add(message);
					}
				});
			});
		} catch (JSONException err) {
			LOGGER.error(err.toString());
		}
		return messages;
	}

	private LocalDate getCurrentDate() {
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(schedulerZoneId));
		return zonedDateTime.toLocalDate();
	}
}
