package br.com.mb.stockmanagerbrokerage.domain.invoice;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface  InvoiceRepository  extends CrudRepository<Invoice, Long> {
	Optional<Invoice> findByCodeAndOwner(String code, String owner);
	Iterable<Invoice> findAllByOwnerOrderByOperationDateAsc(String owner);
}
