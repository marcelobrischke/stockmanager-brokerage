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

import org.junit.jupiter.api.Test;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.keycloakmock.junit5.KeycloakMockExtension;

import br.com.mb.stockmanagerbrokerage.domain.invoice.InvoiceDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WebTest {

	private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
	@RegisterExtension
	static KeycloakMockExtension mock = new KeycloakMockExtension(
			aServerConfig().withPort(8383).withRealm("stockmanager").build());

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listAll() throws Exception {
		String token = mock.getAccessToken(aTokenConfig().withRealmRole("stockmanager-app-users").withResourceRole("stockmanager-backend", "stockmanager-users").build());
		this.mockMvc
				.perform(get("/v1/")
				.header("Authorization", "Bearer " + token))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(startsWith("[")))
				.andExpect(content().string(endsWith("]")));
	}

	@Test
	void listByYear() throws Exception {
		String token = mock.getAccessToken(aTokenConfig().withRealmRole("stockmanager-app-users").withResourceRole("stockmanager-backend", "stockmanager-users").build());
		this.mockMvc.perform(get("/v1/filter/year/2020").header("Authorization", "Bearer " + token)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(startsWith("["))).andExpect(content().string(endsWith("]")));
		// TODO : pending filter
	}

	@Test
	void insert() throws Exception {
		String token = mock.getAccessToken(aTokenConfig().withRealmRole("stockmanager-app-users").withResourceRole("stockmanager-backend", "stockmanager-users").build());
		String tokenAdmin = mock.getAccessToken(aTokenConfig().withRealmRole("stockmanager-app-admins").withResourceRole("stockmanager-backend", "stockmanager-admins").build());
		// given
		String code = "new";
		String symbol = "SYMB3";
		InvoiceDto invoice = new InvoiceDto(code, sdfDate.parse("01/01/2017"), "BUY", symbol, 2896,
				new BigDecimal("24.69"));
		InvoiceDto invoiceNotNew = new InvoiceDto(code, sdfDate.parse("01/01/2017"), "BUY", symbol, 2896,
				new BigDecimal("24.69"));

		// when

		// then
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1/" + code).header("Authorization", "Bearer " + tokenAdmin).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		mockMvc.perform(MockMvcRequestBuilders.post("/v1/").header("Authorization", "Bearer " + token).content(asJsonString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.get("/v1/" + code).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.symbol").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.symbol", is(symbol)));

		mockMvc.perform(MockMvcRequestBuilders.post("/v1/").header("Authorization", "Bearer " + token).content(asJsonString(invoiceNotNew))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isSeeOther());

		mockMvc.perform(MockMvcRequestBuilders.put("/v1/").header("Authorization", "Bearer " + token).content(asJsonString(invoiceNotNew))
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