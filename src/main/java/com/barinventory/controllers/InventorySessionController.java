package com.barinventory.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.barinventory.config.SecurityUtils;
import com.barinventory.entities.InventorySession;
import com.barinventory.services.InventorySessionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class InventorySessionController {

    private final InventorySessionService sessionService;

    // GET /sessions/create-page
    @GetMapping("/create-page")
    public String createSessionPage(Model model) {
        Long barId = SecurityUtils.getBarId();
        model.addAttribute("barId", barId);
        return "session/create-session";
    }

    // POST /sessions/create
    @PostMapping("/create")
    public String createSession(@RequestParam String sessionName) {
        Long barId = SecurityUtils.getBarId();
        InventorySession session = sessionService.createSession(barId, sessionName);
        return "redirect:/sessions/dashboard/" + session.getSessionId();
    }

    // GET /sessions/dashboard/{sessionId}
    @GetMapping("/dashboard/{sessionId}")
    public String sessionDashboard(@PathVariable Long sessionId, Model model) {
        Long barId = SecurityUtils.getBarId();
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("barId", barId);
        return "session/session-dashboard";
    }

    // GET /sessions/summary/{sessionId}
    @GetMapping("/summary/{sessionId}")
    public String sessionSummary(@PathVariable Long sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("stockroomSales", 5000);
        model.addAttribute("wellSales", 4500);
        return "reports/session-summary";
    }

    // POST /sessions/close/{sessionId}
    @PostMapping("/close/{sessionId}")
    public String closeSession(@PathVariable Long sessionId) {
        sessionService.closeSession(sessionId);
        return "redirect:/sessions/create-page";
    }
}