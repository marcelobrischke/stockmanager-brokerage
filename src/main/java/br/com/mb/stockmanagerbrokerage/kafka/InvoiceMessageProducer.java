package br.com.mb.stockmanagerbrokerage.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceMessageProducer {	
	private final KafkaTemplate<String, String> invoiceAddKafkaTemplate;
	private final KafkaTemplate<String, String> invoiceResetKafkaTemplate;

    @Value(value = "invoiceAdd")
    private String topicInvoiceAdd;


    @Value(value = "invoiceReset")
    private String topicInvoiceReset;

	public void sendAdd(InvoiceMessage invoiceMessage){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String message = objectMapper.writeValueAsString(invoiceMessage);
			invoiceAddKafkaTemplate.send(topicInvoiceAdd, message);
		} catch (Exception e) {
			log.error("sendAdd Exception", e);
		}
	}
	
	public void sendReset(String username) {
		String message = username;
		invoiceResetKafkaTemplate.send(topicInvoiceReset, message);
	}

}