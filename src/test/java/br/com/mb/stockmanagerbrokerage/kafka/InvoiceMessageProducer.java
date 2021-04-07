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

    @Value(value = "invoiceAdd")
    private String topicInvoiceAdd;


    @Value(value = "invoiceReset")
    private String topicInvoiceReset;

	public void sendAdd(InvoiceMessage invoiceMessage){

	}
	
	public void sendReset(String username) {

	}

}