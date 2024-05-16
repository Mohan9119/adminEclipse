package com.example.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ProductController {
	@Autowired
	private ProductRepository productRepository;
	
	@PostMapping// create product
	public ResponseEntity<ProductSaveDTO> save (@RequestBody Product product)
	{
		ProductSaveDTO dto = new ProductSaveDTO();
		dto.setStatus(false);
		dto.setMessage("some thing went wrong while saving product");
		productRepository.save(product);
		dto.setStatus(true);
		dto.setMessage(" product saved sucessfully");
		return ResponseEntity.ok(dto);
	}
	@PutMapping  //update product
	public ResponseEntity<ProductSaveDTO> update (@RequestBody Product product)
	{
		ProductSaveDTO dto = new ProductSaveDTO();
		dto.setStatus(false);
		dto.setMessage("some thing went wrong while UPDATING product");
		productRepository.save(product);
		dto.setStatus(true);
		dto.setMessage(" product UPDATED  sucessfully");
		return ResponseEntity.ok(dto);
	}
	@GetMapping("all") //reading  alll products
	public ResponseEntity<Iterable<Product>> readAll ()
	{
		return ResponseEntity.ok(productRepository.findAll());
	}
	@GetMapping("/{id}") // only one record
	public ResponseEntity<Product> read (@PathVariable Integer id)
	{
		return ResponseEntity.ok(productRepository.findById(id).get());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Integer> delete(@PathVariable Integer id)
	{
		productRepository.deleteById(id);
		return ResponseEntity.ok(id);
	}
	
	
	
	
	
}
/*
 CRUD
 
 */
