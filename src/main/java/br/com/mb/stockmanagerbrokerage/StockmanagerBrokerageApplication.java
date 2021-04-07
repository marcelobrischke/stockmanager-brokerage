package br.com.mb.stockmanagerbrokerage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class StockmanagerBrokerageApplication{
    
	public static void main(String[] args) {
		SpringApplication.run(StockmanagerBrokerageApplication.class, args);
	}

}
