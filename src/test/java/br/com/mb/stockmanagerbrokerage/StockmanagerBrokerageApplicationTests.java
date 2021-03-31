package br.com.mb.stockmanagerbrokerage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import br.com.mb.stockmanagerbrokerage.ctrl.BrokerageController;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class StockmanagerBrokerageApplicationTests {

	@Autowired
	private BrokerageController controller;
	
	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
