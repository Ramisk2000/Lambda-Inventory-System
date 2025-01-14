package com.bigdata.dashboard.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bigdata.dashboard.entity.CategoryStock;
import com.bigdata.dashboard.repository.CategoryStockRepository;

@Service
public class AverageService {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private CategoryStockRepository categoryStockRepository;

	// Method sends aggregated stock data every 15 seconds
	@Scheduled(fixedRate = 15000)
	public void trigger() {
		System.out.println("Triggering average stock calculation");

		// Fetch all category stock data
		List<CategoryStock> categoryStocks = categoryStockRepository.findAll();

		// Send data to UI
		this.template.convertAndSend("/topic/average", categoryStocks);
	}
}
