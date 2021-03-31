package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface  InvoiceRepository  extends CrudRepository<Invoice, Long> {
	Optional<Invoice> findByCode(String code);
	void deleteByCode(String code);
}
