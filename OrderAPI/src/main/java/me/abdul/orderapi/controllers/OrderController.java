package me.abdul.orderapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Operation(summary = "order plane ticket")
    @PostMapping("/order")
    public String orderPlaneTicket() {
        return "hello world";
    }


}


