package br.com.compassouol.product.ms.controller;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.com.compassouol.product.ms.model.Product;
import br.com.compassouol.product.ms.repository.ProductRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping({ "/products" })
@Api(value = "Product")
public class ProductController {
	private ProductRepository productRepository;

	public ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@GetMapping
	@ApiOperation(value = "Get a list with all products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, response = Product.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved Product list"),
			@ApiResponse(code = 404, message = "The Product list you were trying to reach is not found") })
	public List findAll() {
		return productRepository.findAll();
	}

	/**
	 * This method search in database a valid product list filtering by received
	 * params. All params can be empty, but this method gonna return a list with all
	 * products if all params are empty.
	 * 
	 * @param query
	 * @param minPrice
	 * @param maxPrice
	 * @return
	 */
	@GetMapping(path = { "/search" })
	@ApiOperation(value = "Get a list witrh filted products by params min_price, max_price and q", response = Product.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved Product list"),
			@ApiResponse(code = 404, message = "The Product list you were trying to reach is not found") })
	public List findByFilters(@RequestParam(name = "q", required = false) String query,
			@RequestParam(name = "min_price", required = false) BigDecimal minPrice,
			@RequestParam(name = "max_price", required = false) BigDecimal maxPrice) {
		return productRepository.findAll().stream().filter(product -> {
			return (query != null) ? (product.getDescription().contains(query) || product.getName().contains(query))
					: true;
		}).filter(product -> (minPrice != null) ? minPrice.compareTo(product.getPrice()) <= 0 : true)
				.filter(product -> (maxPrice != null) ? maxPrice.compareTo(product.getPrice()) >= 0 : true)
				.collect(Collectors.toList());
	}

	/**
	 * This method receive a id and search in database a product by param
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(path = { "/{id}" })
	@ApiOperation(value = "Get a product searching by id", response = Product.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved Product"),
			@ApiResponse(code = 404, message = "The Product you are trying to reach is not found") })
	public ResponseEntity<Product> findById(@PathVariable String id) {
		return productRepository.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * This method receive a http post request and create a product in database.
	 * params name and description cant be null or empty. price param cant be less
	 * then zero
	 * 
	 * @param product
	 * @param result
	 * @return
	 */
	@PostMapping
	@ApiOperation(value = "Create a product and valid params name, description and price", response = Product.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successfully created Product"),
			@ApiResponse(code = 400, message = "Bad request, probaly invalid or empty param") })
	public ResponseEntity<?> create(@RequestBody @Valid Product product, BindingResult result) {
		System.out.println(result);
		if (result.hasErrors())
			return this.generateErrorMessaget(result.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
		Product created = productRepository.save(product);
		return ResponseEntity.status(HttpStatus.CREATED.value()).body(created);
	}

	/**
	 * This method receive a http put request and update a product in database
	 * searching by product id param. params name and description cant be null or
	 * empty. price param cant be less then zero
	 * 
	 * @param id
	 * @param product
	 * @param result
	 * @return
	 */
	@PutMapping(value = "/{id}")
	@ApiOperation(value = "Update a product searching by id and valid params name, description and price", response = Product.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully updated Product"),
			@ApiResponse(code = 400, message = "Bad request, probaly invalid or empty param") })
	public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody Product product,
			BindingResult result) {
		;
		if (result.hasErrors())
			return this.generateErrorMessaget(result.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST.value());

		return productRepository.findById(id).map(record -> {
			record.setName(product.getName());
			record.setPrice(product.getPrice());
			record.setDescription(product.getDescription());

			Product updated = productRepository.save(record);
			return ResponseEntity.status(HttpStatus.OK).body(updated);
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping(path = { "/{id}" })
	@ApiOperation(value = "Delete a product searching by id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted Product"),
			@ApiResponse(code = 404, message = "The Product you are trying to delete is not found") })
	public ResponseEntity<?> delete(@PathVariable("id") String id) {
		return productRepository.findById(id).map(record -> {

			productRepository.deleteById(id);

			return ResponseEntity.ok().build();
		}).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * This method generate a message error to invalid http requests
	 * 
	 * @param result
	 * @param status
	 * @return
	 */
	private ResponseEntity<?> generateErrorMessaget(String message, int status) {

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("status_code", status);
		map.put("message", message);
		return ResponseEntity.status(status).body(map);
	}
	
	   @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
	    public ResponseEntity<?> handleExceptionParseJsonError(RuntimeException ex, WebRequest request) {
		   return this.generateErrorMessaget(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST.value());
	    }
}
