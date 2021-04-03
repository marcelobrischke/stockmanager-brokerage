package br.com.mb.stockmanagerbrokerage.ctrl;

import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.mb.stockmanagerbrokerage.domain.invoice.InvoiceDto;
import br.com.mb.stockmanagerbrokerage.domain.invoice.InvoiceService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/invoice")
@CrossOrigin("*")
public class BrokerageController {

	@Autowired
	private InvoiceService service;

	@RolesAllowed({ "stockmanager-users", "stockmanager-admins" })
	@GetMapping("/")
	public List<InvoiceDto> listAll() {

		List<InvoiceDto> invoices = service.listAll();

		log.debug(invoices.toString());
		return invoices;
	}

	@RolesAllowed({ "stockmanager-users", "stockmanager-admins" })
	@GetMapping("/filter/year/{year}")
	public List<InvoiceDto> listByYear(@PathVariable Integer year) {
		log.debug("Year: " + year);

		// TODO: filters
		List<InvoiceDto> invoices = service.listAll();
		log.debug(invoices.toString());
		return invoices;
	}

	@RolesAllowed({ "stockmanager-users", "stockmanager-admins" })
	@PostMapping(value = "/")
	@ResponseStatus(value = HttpStatus.CREATED)
	public void add(@Valid @RequestBody InvoiceDto invoiceDto) {
		log.debug("InvoiceDto: " + invoiceDto);
		Optional<InvoiceDto> invoiceRet = service.storeInvoiceNoOverrideIfExists(invoiceDto);
		if (invoiceRet.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.ACCEPTED, "Already exists!");
		}
	}

	@RolesAllowed({ "stockmanager-users", "stockmanager-admins" })
	@PutMapping(value = "/")
	@ResponseStatus(value = HttpStatus.OK)
	public void overwrite(@Valid @RequestBody InvoiceDto invoiceDto) {
		log.debug("InvoiceDto: " + invoiceDto);
		service.storeInvoiceOverrideIfExists(invoiceDto);
	}

	@RolesAllowed({ "stockmanager-users", "stockmanager-admins" })
	@DeleteMapping("/{code}")
	@ResponseStatus(value = HttpStatus.OK)
	public void remove(@PathVariable String code) {
		log.debug("Code: " + code);

		service.remove(code);
	}

	@RolesAllowed({ "stockmanager-users", "stockmanager-admins" })
	@GetMapping("/{code}")
	@ResponseStatus(value = HttpStatus.OK)
	public Optional<InvoiceDto> invoice(@PathVariable String code) {
		log.debug("Code: " + code);
		Optional<InvoiceDto> invoiceRet = service.getInvoice(code);
		if (invoiceRet.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT);
		}
		return invoiceRet;
	}
}