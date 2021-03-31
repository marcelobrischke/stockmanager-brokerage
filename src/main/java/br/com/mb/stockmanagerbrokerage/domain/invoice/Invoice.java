package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date operationDate;
	@NotNull
	private String operation; // TODO: extract to enum
	@NotNull
	@Size(min=5, max=5)
	private String symbol;
	@NotNull
	@Min(value = 1, message = "Quantity should not be less than 1")
	private Integer quantity;
	@NotNull
	@Min(value = 1, message = "UnitaryValue should not be less than 1")
	private BigDecimal unitaryValue;
	// TODO: include additional secondary data

	public Invoice(String code, Date operationDate, String operation, String symbol, Integer quantity,
			BigDecimal unitaryValue) {
		super();
		this.code = code;
		this.operationDate = operationDate;
		this.operation = operation;
		this.symbol = symbol;
		this.quantity = quantity;
		this.unitaryValue = unitaryValue;
	}
	
	

}
