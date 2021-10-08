package com.example.cowin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class SendWhatsAppMessageService {
	// Replace these placeholders with your Account Sid and Auth Token
	public static final String ACCOUNT_SID = "ACc37a481232bf168efdda96bd92a35822";
	public static final String AUTH_TOKEN = "b23e2e0d220841bf6c85b594f2f275fb";

	public String sendMessage(List<String> messages) {
		// SMS can contain only 1600 characters, so we will limit it to avoid exception
		String message = messages.toString();
		if (message.length() >= 1600)
			message = message.substring(0, 1590);
		messages.forEach(m -> System.out.println(m));
		//Uncomment this once your twilio setting is done.
		send(message);
		sms(message);
		return message;
	}

	private void sms(String m){
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		Message message = Message
				.creator(new PhoneNumber("+918660431106"), // to
						new PhoneNumber("+18595453405"), // from
						"qwertyuiop")
				.create();
		message.getSid();
	}

	private void send(String message) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message.creator(new PhoneNumber("whatsapp:+918660431106"), new PhoneNumber("whatsapp:+14155238886"), message)
				.create();

	}
}