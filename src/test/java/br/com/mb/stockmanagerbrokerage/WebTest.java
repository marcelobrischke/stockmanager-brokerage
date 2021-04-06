package br.com.mb.stockmanagerbrokerage;

import static com.tngtech.keycloakmock.api.ServerConfig.aServerConfig;
import static com.tngtech.keycloakmock.api.TokenConfig.aTokenConfig;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.keycloakmock.junit5.KeycloakMockExtension;

import br.com.mb.stockmanagerbrokerage.domain.invoice.InvoiceDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
class WebTest {

	private static final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
	@RegisterExtension
	static KeycloakMockExtension mock = new KeycloakMockExtension(
			aServerConfig().withPort(8383).withRealm("stockmanager").build());;

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;
	
	private static String token;
	private static String tokenAdmin;
	private static String code = "new";
	private static String symbol = "SYMB3";
	private static InvoiceDto invoice;
	private static InvoiceDto invoiceNotNew;
	
	@BeforeAll
	static void init() throws Exception {
		token = mock.getAccessToken(aTokenConfig().withRealmRole("stockmanager-app-users").withResourceRole("stockmanager-backend", "stockmanager-users").withPreferredUsername("testUser").build());
		tokenAdmin = mock.getAccessToken(aTokenConfig().withRealmRole("stockmanager-app-admins").withResourceRole("stockmanager-backend", "stockmanager-admins").withPreferredUsername("testAdmin").build());
		

		invoice = new InvoiceDto(code, sdfDate.parse("01/01/2017"), "BUY", symbol, 2896,
				new BigDecimal("24.69"));
		invoiceNotNew = new InvoiceDto(code, sdfDate.parse("01/01/2017"), "BUY", symbol, 2896,
				new BigDecimal("24.69"));
	}

	@Test
	@Order(6)
	void listAll() throws Exception {
		this.mockMvc
				.perform(get("/invoice/")
				.header("Authorization", "Bearer " + token))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(startsWith("[")))
				.andExpect(content().string(endsWith("]")));
	}

	@Test
	@Order(7)
	void listByYear() throws Exception {
		this.mockMvc.perform(get("/invoice/filter/year/2020").header("Authorization", "Bearer " + token)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(startsWith("["))).andExpect(content().string(endsWith("]")));
		// TODO : pending filter
	}

	@Test
	@Order(1)
	void delete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/invoice/" + code).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	@Order(2)
	void insert() throws Exception {
		//TODO: verify all attributes
		mockMvc.perform(MockMvcRequestBuilders.post("/invoice/").header("Authorization", "Bearer " + token).content(asJsonString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	@Test
	@Order(3)
	void getOneInvoice() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/invoice/" + code).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.symbol").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.symbol", is(symbol)));
	}
	
	@Test
	@Order(4)
	void insertRepeated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/invoice/").header("Authorization", "Bearer " + token).content(asJsonString(invoiceNotNew))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted());
	}

	@Test
	@Order(5)
	void update() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/invoice/").header("Authorization", "Bearer " + token).content(asJsonString(invoiceNotNew))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}