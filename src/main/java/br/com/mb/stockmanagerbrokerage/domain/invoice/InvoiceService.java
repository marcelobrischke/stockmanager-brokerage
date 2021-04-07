package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mb.stockmanagerbrokerage.domain.user.UserService;
import br.com.mb.stockmanagerbrokerage.kafka.InvoiceMessage;
import br.com.mb.stockmanagerbrokerage.kafka.InvoiceMessageProducer;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvoiceService {

	@Autowired
	private InvoiceRepository repository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private InvoiceMessageProducer producer;

	public List<InvoiceDto> listAll() {
		log.debug("User: " +userService.getUsername());
		Iterable<Invoice> invoices = repository.findAllByOwnerOrderByOperationDateAsc(userService.getUsername());
		List<InvoiceDto> invoicesDto = new ArrayList<>();
		for (Invoice invoice : invoices) {

			InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
					invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
			invoicesDto.add(invoiceDto);
		}
		return invoicesDto;
	}

	public Optional<InvoiceDto> getInvoice(String code) {
		log.debug("User: " +userService.getUsername());
		return repository.findByCodeAndOwner(code, userService.getUsername())
				.map(invoice -> new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
						invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue()));
	}

	public Optional<InvoiceDto> storeInvoiceNoOverrideIfExists(InvoiceDto invoiceDto) {
		log.debug("User: " +userService.getUsername());
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
			
			producer.sendAdd(invoiceToMsg(invoice));
			
			return invoiceToDto(invoice);
		}
	}
	
	public Optional<InvoiceDto> storeInvoiceOverrideIfExists(InvoiceDto invoiceDto) {
		log.debug("User: " +userService.getUsername());
		Optional<Invoice> invoiceStored = repository.findByCodeAndOwner(invoiceDto.getCode(), userService.getUsername());

		Invoice invoice;
		if (invoiceStored.isPresent()) {
			log.debug("isPresent, stored:"+ invoiceStored.toString());
			log.debug("will override");
			invoice = invoiceStored.get();
			BeanUtils.copyProperties(invoiceDto, invoice);
			invoice = repository.save(invoice);
			restartWallet();
		} else {
			log.debug("it is not present, will create");
			invoice = new Invoice();
			BeanUtils.copyProperties(invoiceDto, invoice);
			invoice.setOwner(userService.getUsername());			
			invoice = repository.save(invoice);
			producer.sendAdd(invoiceToMsg(invoice));
		}
		return invoiceToDto(invoice);
	}

	private void restartWallet() {
		producer.sendReset(userService.getUsername());
		
		Iterable<Invoice> invoices = repository.findAllByOwnerOrderByOperationDateAsc(userService.getUsername());
		for (Invoice invoice : invoices) {
			producer.sendAdd(invoiceToMsg(invoice));
		}
	}
	
	private Optional<InvoiceDto> invoiceToDto(Invoice invoice) {
		InvoiceDto invoiceDto = new InvoiceDto(invoice.getCode(), invoice.getOperationDate(),
				invoice.getOperation(), invoice.getSymbol(), invoice.getQuantity(), invoice.getUnitaryValue());
		return Optional.of(invoiceDto);
	}
	
	private InvoiceMessage invoiceToMsg(Invoice invoice) {
		InvoiceMessage msg = new InvoiceMessage();
		msg.setCode(invoice.getCode());
		msg.setOperationDate(invoice.getOperationDate());
		msg.setOperation(invoice.getOperation());
		msg.setSymbol(invoice.getSymbol());
		msg.setQuantity(invoice.getQuantity());
		msg.setUnitaryValue(invoice.getUnitaryValue());
		msg.setUsername(invoice.getOwner());

		return msg;
	}
	
	public void remove(String code) {
		log.debug("User: " +userService.getUsername());
		Optional<Invoice> invoice = repository.findByCodeAndOwner(code, userService.getUsername());
		if (invoice.isPresent()) {
			repository.delete(invoice.get());
			
			restartWallet();
		}
	}
}
