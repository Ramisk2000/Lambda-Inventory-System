package com.bigdata.dashboard.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bigdata.dashboard.entity.InventoryData;
import com.bigdata.dashboard.entity.LowStock;
import com.bigdata.dashboard.entity.CategoryStock;
import com.bigdata.dashboard.repository.InventoryDataRepository;
import com.bigdata.dashboard.repository.LowStockRepository;
import com.bigdata.dashboard.repository.CategoryStockRepository;

/**
 * Service class to send data messages to dashboard UI at fixed intervals using WebSocket.
 */
@Service
public class DataService {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private InventoryDataRepository inventoryDataRepository;

	@Autowired
	private LowStockRepository lowStockRepository;

	@Autowired
	private CategoryStockRepository categoryStockRepository;

	/**
	 * Method to send real-time inventory data every 10 seconds.
	 */
	@Scheduled(fixedRate = 10000)
	public void triggerInventoryData() {
		List<InventoryData> inventoryData = inventoryDataRepository.findAll();
		System.out.println("Fetched Inventory Data: " + inventoryData);
		this.template.convertAndSend("/topic/data", inventoryData);
	}




	/**
	 * Method to send low-stock items every 15 seconds.
	 */
	@Scheduled(fixedRate = 15000)
	public void triggerLowStockData() {
		System.out.println("Triggering low stock data...");

		// Fetch all low-stock items from the repository
		List<LowStock> lowStockItems = lowStockRepository.findAll();

		// Send low-stock items to the UI
		this.template.convertAndSend("/topic/lowStock", lowStockItems);
	}

	/**
	 * Method to send aggregated category stock data every 20 seconds.
	 */
	@Scheduled(fixedRate = 20000)
	public void triggerCategoryStockData() {
		List<CategoryStock> categoryStockItems = categoryStockRepository.findAll();
		System.out.println("Fetched Category Stock Data: " + categoryStockItems);
		this.template.convertAndSend("/topic/average", categoryStockItems);
	}
}
