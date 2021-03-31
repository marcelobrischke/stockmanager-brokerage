package br.com.mb.stockmanagerbrokerage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;

import br.com.mb.stockmanagerbrokerage.domain.invoice.Invoice;

public class InvoiceTest {
	
	@Test
	public void getSetTest() {
		Long id = 3L;
		String code = "code";
		Date operationDate = new Date();
		String operation = "BUY";
		String symbol = "SYM3";
		Integer quantity = 100;
		BigDecimal unitaryValue = new BigDecimal("24.65");

		Invoice invoice = new Invoice();
		invoice.setId(id);
		invoice.setCode(code);
		invoice.setOperationDate(operationDate);
		invoice.setOperation(operation);
		invoice.setSymbol(symbol);
		invoice.setQuantity(quantity);
		invoice.setUnitaryValue(unitaryValue);
		
		assertEquals(invoice.getId(), id);
		assertEquals(invoice.getCode(), code);
		assertEquals(invoice.getOperationDate(), operationDate);
		assertEquals(invoice.getOperation(), operation);
		assertEquals(invoice.getSymbol(), symbol);
		assertEquals(invoice.getQuantity(), quantity);
		assertEquals(invoice.getUnitaryValue(), unitaryValue);
	}
}
