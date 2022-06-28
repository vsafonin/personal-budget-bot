package ru.vladimir.personalAccounterBot.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductData {

	private BigDecimal cost;
	
	private BigDecimal quantity;
	
	private Product product;
	
}
