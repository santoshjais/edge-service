package com.oms.edgeservice;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
//import org.springframework.hateoas.EntityModel;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
class Item {
    private String name;
    public void setName(String name) { 
        this.name = name;
    }

}


@org.springframework.cloud.openfeign.EnableFeignClients
@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication
public class EdgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceApplication.class, args);
    }
}


@FeignClient("item-catalog-service")
interface ItemClient {

@GetMapping("/items")
EntityModel<Item> readItems();

}

@RestController
class GoodItemApiAdapterRestController {

    private final ItemClient itemClient;

    public GoodItemApiAdapterRestController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping("/top-brands")
    public Collection<Item> goodItems() {
    	
     		
        return ((Collection<Item>) itemClient.readItems().getContent()).stream().filter(this::isGreat)
                .collect(Collectors.toList());
    }

    
    
    private boolean isGreat(Item item) {
        return !item.getName().equals("Nike") &&
                !item.getName().equals("Adidas") &&
                !item.getName().equals("Reebok");
    }
}

