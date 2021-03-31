package br.com.mb.stockmanagerbrokerage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import br.com.mb.stockmanagerbrokerage.domain.invoice.Invoice;
import br.com.mb.stockmanagerbrokerage.domain.invoice.InvoiceRepository;

@DataJpaTest
class InvoiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InvoiceRepository repository;

    @Test
    void testFindByCode() throws ParseException {

    	SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        entityManager.persist(new Invoice("test", sdfDate.parse("01/01/2017"), "BUY", "TEST3", 2896, new BigDecimal("24.69")));

        Optional<Invoice> invoice = repository.findByCode("test");
        assertEquals(true, invoice.isPresent());
        assertEquals("test", invoice.get().getCode());
    }

}