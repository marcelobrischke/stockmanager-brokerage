package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Invoice {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(unique = true)
	private String code;
	@NotNull
	private Date operationDate;
	@NotNull
	private String operation; // TODO: extract to enum
	@NotNull
	private String symbol;
	@NotNull
	private Integer quantity;
	@NotNull
	private BigDecimal unitaryValue;
	// TODO: include additional secondary data
	@NotNull
	private String owner;

	public Invoice(String code, Date operationDate, String operation, String symbol, Integer quantity,
			BigDecimal unitaryValue, String owner) {
		super();
		this.code = code;
		this.operationDate = operationDate;
		this.operation = operation;
		this.symbol = symbol;
		this.quantity = quantity;
		this.unitaryValue = unitaryValue;
		this.owner = owner;
	}
	
	

}
