package br.com.compassouol.product.ms.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Product {

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@ApiModelProperty(notes = "The product id", required = true)
	private String id;
	
	@NotNull(message = "Name cant be null")
	@NotEmpty(message = "Name is mandatory")
	@ApiModelProperty(notes = "The name of the product", required = true)
	private String name;
	
	@NotNull(message = "Description cant be null")
	@NotEmpty(message = "Description is mandatory")
	@ApiModelProperty(notes = "A short description to the product", required = true)
	private String description;
	
    @Positive(message = "Price must be positive")
    @ApiModelProperty(notes = "The price of the product must be positive", required = false)
	private BigDecimal price;	


}
