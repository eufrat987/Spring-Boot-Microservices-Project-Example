package com.eufrat.inventory_service;

import com.eufrat.inventory_service.model.Inventory;
import com.eufrat.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return _ -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iphone_13_blue");
			inventory.setQuantity(100);
			inventoryRepository.save(inventory);

			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("iphone_13_green");
			inventory2.setQuantity(0);
			inventoryRepository.save(inventory2);
		};
	}
}
