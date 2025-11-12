package com.sistem.monitoring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sistem.monitoring.models.DailyJournal;
import com.sistem.monitoring.services.DailyJournalService;
import com.sistem.monitoring.services.PlacementService;

@Controller
@RequestMapping("/daily-journal")
public class DailyJournalController {
    

    @Autowired
    private DailyJournalService dailyJournalService;
    @Autowired
    private PlacementService placementService;

    @GetMapping
    public String showAll (Model model){
        model.addAttribute("journal", dailyJournalService.getAllJournal());
        return "DailyJournalView/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Long id, Model model){
        DailyJournal journal = new DailyJournal();
        model.addAttribute("journal", journal);
        model.addAttribute("placement", placementService.getAllPlacement());
        return "DailyJournalView/create-form";

    }

    @PostMapping
    public String saveData(@ModelAttribute DailyJournal journal){
        dailyJournalService.createJournal(journal);
        return "redirect:/daily-journal";
    }
}
