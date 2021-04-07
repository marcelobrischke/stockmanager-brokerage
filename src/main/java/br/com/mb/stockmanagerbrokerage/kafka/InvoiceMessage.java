package br.com.mb.stockmanagerbrokerage.kafka;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class InvoiceMessage {

	private String code;
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date operationDate;
	private String operation;
	private String symbol;
	private Integer quantity;
	private BigDecimal unitaryValue;
	private String username;

}