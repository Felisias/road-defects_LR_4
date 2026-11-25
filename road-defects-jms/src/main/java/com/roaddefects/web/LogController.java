package com.roaddefects.web;

import com.roaddefects.service.ChangeLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogController {

    private final ChangeLogService changeLogService;

    public LogController(ChangeLogService changeLogService) {
        this.changeLogService = changeLogService;
    }

    @GetMapping("/log")
    public String log(Model model) {
        model.addAttribute("entries", changeLogService.findLatest());
        return "log";
    }
}
