package br.com.compassouol.product.ms.test.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.compassouol.product.ms.controller.ProductController;
import br.com.compassouol.product.ms.model.Product;
import br.com.compassouol.product.ms.repository.ProductRepository;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper obm;

	@MockBean
	ProductRepository repository;

	@MockBean
	BindingResult result;

	@Test
	public void testCreateProductShouldReturnStatusCode201AndValidProduct() throws JsonProcessingException, Exception {
		
		Product actualProduct = new Product();
		actualProduct.setDescription("Description");
		actualProduct.setName("name");
		actualProduct.setPrice(new BigDecimal(10));

		Product productExpected = new Product();
		productExpected.setDescription("Description");
		productExpected.setName("name");
		productExpected.setPrice(new BigDecimal(10));
		productExpected.setId("1");

		Mockito.when(result.hasErrors()).thenReturn(false);
		Mockito.when(repository.save(actualProduct)).thenReturn(productExpected);

		String url = "/products";

		MvcResult mvcResult = mvc.perform(
				 post(url)
				 .contentType("application/json")
				 .content(obm.writeValueAsString(actualProduct)))
				.andExpect(status().isCreated()).andReturn(); 
		
		String actualResponse = mvcResult.getResponse().getContentAsString();
		String expectedResponse = obm.writeValueAsString(productExpected);
		
		assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);

	}
	
	@Test
	public void tesGettListProductsShouldReturnStatusCode200AndProductList() throws Exception {
		
		List<Product> products = new ArrayList<Product>();
		
		Product p1 = new Product();
		p1.setDescription("Description");
		p1.setId("1");
		p1.setName("name");
		p1.setDescription("Description");
		
		
		Product p2 = new Product();
		p2.setDescription("Description");
		p2.setId("2");
		p2.setName("name");
		p2.setDescription("Description");
		
		products.add(p1);
		products.add(p2);
		
		Mockito.when(repository.findAll()).thenReturn(products);
		
		String url = "/products";
		
		MvcResult mvcResult = mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		String actualResponse = mvcResult.getResponse().getContentAsString();
		String expectedResponse = obm.writeValueAsString(products);
		
		assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
		
	}
	
	@Test
	public void testUpdateProducShouldReturnStatusCode200AndUpdatedProduct() throws JsonProcessingException, Exception {
		
		Product atual = new Product();
		atual.setDescription("Description");
		atual.setId("1");
		atual.setName("name");
		atual.setDescription("Description");
		
		Product expectedProduct = new Product();
		expectedProduct.setDescription("Description new");
		expectedProduct.setId("1");
		expectedProduct.setName("name new");
		
		Optional<Product> opt =  Optional.of(atual);
		
		Mockito.when(repository.save(atual)).thenReturn(expectedProduct);
		Mockito.when(repository.findById(atual.getId())).thenReturn(opt);

		String url = "/products/1";
		
		MvcResult mvcResult = mvc.perform(
				 put(url)
				 .contentType("application/json")
				 .content(obm.writeValueAsString(atual)))
				.andExpect(status().isOk())
				.andReturn(); 
		
		String actualResponse = mvcResult.getResponse().getContentAsString();
		String expectedResponse = obm.writeValueAsString(expectedProduct);
		
		assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
	}
	
	@Test
	public void testDeleteProductShouldReturnStatusCode200() throws Exception {
		
		Product atual = new Product();
		atual.setDescription("Description");
		atual.setId("1");
		atual.setName("name");
		atual.setDescription("Description");
		
		Optional<Product> opt =  Optional.of(atual);
		
		Mockito.when(repository.findById(atual.getId())).thenReturn(opt);
		
		String url = "/products/1";
		
		MvcResult mvcResult = mvc.perform(delete(url)).andExpect(status().isOk()).andReturn();
	}
	
	@Test
	public void testGetProductByQueryParamsShouldReturnFilteredListAndStatusCode200() throws Exception {
		
		List<Product> actualProducts = new ArrayList<Product>();
		List<Product> expectedProducts = new ArrayList<Product>();
		
		Product p1 = new Product();
		p1.setDescription("Description test");
		p1.setId("1");
		p1.setName("name");
		p1.setPrice(new BigDecimal(10));
		
		
		Product p2 = new Product();
		p2.setDescription("Description");
		p2.setId("2");
		p2.setName("name");
		p2.setPrice(new BigDecimal(10));
		
		actualProducts.add(p1);
		actualProducts.add(p2);
		
		expectedProducts.add(p1);
		
		Mockito.when(repository.findAll()).thenReturn(actualProducts);
		
		String url = "/products/search?min_price=10&max_price=40&q=test";
		
		MvcResult mvcResult = mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		String actualResponse = mvcResult.getResponse().getContentAsString();
		String expectedResponse = obm.writeValueAsString(expectedProducts);
		
		assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
	}
}
