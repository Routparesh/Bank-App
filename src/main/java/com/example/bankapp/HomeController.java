package com.example.bankapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        Account account = new Account();
        account.setId(1001L);
        account.setUsername("admin");
        account.setBalance(5000.00);

        model.addAttribute("account", account);

        return "dashboard";
    }


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/transactions")
    public String transactions() {
        return "transactions";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Double amount) {
    
        System.out.println("Deposit Amount: " + amount);
    
        return "redirect:/dashboard";
    }
    
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Double amount) {
    
        System.out.println("Withdraw Amount: " + amount);
    
        return "redirect:/dashboard";
    }
    
    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String toUsername,
            @RequestParam Double amount) {
    
        System.out.println("Transfer " + amount + " to " + toUsername);
    
        return "redirect:/dashboard";
    }
}