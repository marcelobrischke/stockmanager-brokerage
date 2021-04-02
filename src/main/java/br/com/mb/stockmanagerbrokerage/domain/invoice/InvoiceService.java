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

	public List<InvoiceDto> listAll(String username) {
		Iterable<Invoice> invoices = repository.findByOwner(username);
		List<InvoiceDto> invoicesDto = new ArrayList<>();
		for (Invoice invoice : invoices) {

			InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
					invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
			invoicesDto.add(invoiceDto);
		}
		return invoicesDto;
	}

	public Optional<InvoiceDto> getInvoice(String code, String username) {
		return repository.findByCodeAndOwner(code, username)
				.map(invoice -> new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
						invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue()));
	}

	public Optional<InvoiceDto> storeinvoice(InvoiceDto invoiceDto, boolean overrideIfExists, String username) {
		//TODO: improve all this methpd code below, it is messy, but works
		Optional<Invoice> invoiceStored = repository.findByCodeAndOwner(invoiceDto.getCode(), username);

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
			invoice.setOwner(username);
			invoice = repository.save(invoice);
			return invoiceToDto(invoice);
		}
	}

	private Optional<InvoiceDto> invoiceToDto(Invoice invoice) {
		InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
				invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
		return Optional.of(invoiceDto);
	}
	
	public void remove(String code, String username) {
		//TODO: filter delete to just code owned by username
		repository.deleteByCode(code);
	}
}
