package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvoiceService {

	@Autowired
	private InvoiceRepository repository;

	public List<InvoiceDto> listAll() {
		Iterable<Invoice> invoices = repository.findAll();
		List<InvoiceDto> invoicesDto = new ArrayList<>();
		for (Invoice invoice : invoices) {

			InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
					invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
			invoicesDto.add(invoiceDto);
		}
		return invoicesDto;
	}

	public Optional<InvoiceDto> getInvoice(String code) {
		return repository.findByCode(code)
				.map(invoice -> new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
						invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue()));
	}

	public Optional<InvoiceDto> storeinvoice(InvoiceDto invoiceDto, boolean overrideIfExists) {
		//TODO: improve all this methpd code below, it is messy, but works
		Optional<Invoice> invoiceStored = repository.findByCode(invoiceDto.getCode());

		Invoice invoice;
		if (invoiceStored.isPresent()) {
			log.debug("isPresent, stored:"+ invoiceStored.toString());
			if (overrideIfExists) {
				log.debug("will override");
				BeanUtils.copyProperties(invoiceDto, invoiceStored);
				invoice = repository.save(invoiceStored.get());
				return invoiceToDto(invoice);
			} else {
				log.debug("return without override");
				return Optional.empty();
			}
		} else {
			log.debug("is not present, will create");
			invoice = new Invoice();
			BeanUtils.copyProperties(invoiceDto, invoice);
			invoice = repository.save(invoice);
			return invoiceToDto(invoice);
		}
	}

	private Optional<InvoiceDto> invoiceToDto(Invoice invoice) {
		InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
				invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
		return Optional.of(invoiceDto);
	}
	
	public void remove(String code) {
		repository.deleteByCode(code);
	}
}
