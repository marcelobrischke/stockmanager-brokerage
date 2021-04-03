package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mb.stockmanagerbrokerage.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvoiceService {

	@Autowired
	private InvoiceRepository repository;
	
	@Autowired
	private UserService userService;

	public List<InvoiceDto> listAll() {
		Iterable<Invoice> invoices = repository.findByOwner(userService.getUsername());
		List<InvoiceDto> invoicesDto = new ArrayList<>();
		for (Invoice invoice : invoices) {

			InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
					invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
			invoicesDto.add(invoiceDto);
		}
		return invoicesDto;
	}

	public Optional<InvoiceDto> getInvoice(String code) {
		return repository.findByCodeAndOwner(code, userService.getUsername())
				.map(invoice -> new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
						invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue()));
	}

	public Optional<InvoiceDto> storeInvoiceNoOverrideIfExists(InvoiceDto invoiceDto) {
		Optional<Invoice> invoiceStored = repository.findByCodeAndOwner(invoiceDto.getCode(), userService.getUsername());

		Invoice invoice;
		if (invoiceStored.isPresent()) {
			log.debug("isPresent, stored:"+ invoiceStored.toString());
			log.debug("return without override");
			return Optional.empty();
		} else {
			log.debug("it is not present, will create");
			invoice = new Invoice();
			BeanUtils.copyProperties(invoiceDto, invoice);
			invoice.setOwner(userService.getUsername());
			invoice = repository.save(invoice);
			return invoiceToDto(invoice);
		}
	}
	
	public Optional<InvoiceDto> storeInvoiceOverrideIfExists(InvoiceDto invoiceDto) {
		Optional<Invoice> invoiceStored = repository.findByCodeAndOwner(invoiceDto.getCode(), userService.getUsername());

		Invoice invoice;
		if (invoiceStored.isPresent()) {
			log.debug("isPresent, stored:"+ invoiceStored.toString());
			log.debug("will override");
			BeanUtils.copyProperties(invoiceDto, invoiceStored);
			invoice = repository.save(invoiceStored.get());
		} else {
			log.debug("it is not present, will create");
			invoice = new Invoice();
			BeanUtils.copyProperties(invoiceDto, invoice);
			invoice.setOwner(userService.getUsername());
			invoice = repository.save(invoice);
		}
		return invoiceToDto(invoice);
	}

	private Optional<InvoiceDto> invoiceToDto(Invoice invoice) {
		InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
				invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
		return Optional.of(invoiceDto);
	}
	
	public void remove(String code) {
		//TODO: filter delete to just code owned by username
		repository.deleteByCode(code);
	}
}
