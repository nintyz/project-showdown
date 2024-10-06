package com.projectshowdown.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RandomDataController {

    @Autowired
    private RandomDataService randomDataService;

    @PostMapping("/addRandomData")
    public String addRandomData() {
        return randomDataService.addRandomData();
    }
}