package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

//@Value
@Data //TODO: final properties and DTO Date parser without @NoArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
	@NotNull
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

}
