package br.com.compassouol.product.ms.controller;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.compassouol.product.ms.model.Product;
import br.com.compassouol.product.ms.repository.ProductRepository;

@RestController
@RequestMapping({ "/products" })
public class ProductController {
	private ProductRepository productRepository;

	public ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@GetMapping
	public List findAll() {
		return productRepository.findAll();
	}

	@GetMapping(path = { "/search" })
	public List findByFilters(@RequestParam(name = "q", required = false) String query, @RequestParam(name = "min_price", required = false) BigDecimal minPrice,
			@RequestParam(name = "max_price", required = false) BigDecimal maxPrice) {
		return productRepository.findAll().stream()
				.filter(product -> {
					return (query != null) ? (product.getDescription().contains(query) || product.getName().contains(query)) : true;
				})
				.filter(product -> (minPrice != null) ? minPrice.compareTo(product.getPrice()) <= 0 : true)
				.filter(product -> (maxPrice != null) ? maxPrice.compareTo(product.getPrice()) >= 0 : true).collect(Collectors.toList());
	}

	@GetMapping(path = { "/{id}" })
	public ResponseEntity<Product> findById(@PathVariable String id) {
		return productRepository.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Valid Product product, BindingResult result) {
		
		if (result.hasErrors()) return this.generateErrorMessaget(result, HttpStatus.BAD_REQUEST.value());
		Product created = productRepository.save(product);
		return  ResponseEntity.status(HttpStatus.CREATED.value()).body(created);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody Product product, BindingResult result) {
		System.out.println();
		if (result.hasErrors()) return this.generateErrorMessaget(result, HttpStatus.BAD_REQUEST.value());
		
		return productRepository.findById(id).map(record -> {
			record.setName(product.getName());
			record.setPrice(product.getPrice());
			record.setDescription(product.getDescription());

			Product updated = productRepository.save(record);
			return ResponseEntity.status(HttpStatus.OK).body(updated);
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping(path = { "/{id}" })
	public ResponseEntity<?> delete(@PathVariable("id") String id) {
		return productRepository.findById(id).map(record -> {
			
			productRepository.deleteById(id);
			
			return ResponseEntity.ok().build();
		}).orElse(ResponseEntity.notFound().build());
	}
	
	private ResponseEntity<?>  generateErrorMessaget(BindingResult result, int status) {
		
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("status_code", status);
		map.put("message", result.getFieldError().getDefaultMessage());
		return ResponseEntity.status(status).body(map);
	}
	
	

}
