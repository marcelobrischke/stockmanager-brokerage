package br.com.mb.stockmanagerbrokerage;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.mb.stockmanagerbrokerage.domain.invoice.InvoiceDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebTest {

	private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void listAll() throws Exception {
		this.mockMvc.perform(get("/v1/")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(startsWith("[") ))
				.andExpect(content().string(endsWith("]") ));
	}
	
	@Test
	public void listByYear() throws Exception {
		this.mockMvc.perform(get("/v1/filter/year/2020")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(startsWith("[") ))
				.andExpect(content().string(endsWith("]") ));
		//TODO : pending filter
	}
	
	@Test
	public void insert() throws Exception {		
		// given
		String code = "new";
		String symbol = "SYMB3";
		InvoiceDto invoice = new InvoiceDto(code, sdfDate.parse("01/01/2017"), "BUY", symbol, 2896, new BigDecimal("24.69"));
		InvoiceDto invoiceNotNew = new InvoiceDto(code, sdfDate.parse("01/01/2017"), "BUY", symbol, 2896, new BigDecimal("24.69"));

		// when

		// then
		mockMvc.perform( MockMvcRequestBuilders
				.delete("/v1/"+code)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		mockMvc.perform( MockMvcRequestBuilders
				.post("/v1/")
				.content(asJsonString(invoice))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		mockMvc.perform( MockMvcRequestBuilders
				.get("/v1/"+code)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.symbol").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.symbol", is(symbol)));	
		
		mockMvc.perform( MockMvcRequestBuilders
				.post("/v1/")
				.content(asJsonString(invoiceNotNew))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isSeeOther());
		
		mockMvc.perform( MockMvcRequestBuilders
				.put("/v1/")
				.content(asJsonString(invoiceNotNew))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	private static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
}