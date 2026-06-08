package com.example.bankapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Account getCurrentAccount() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Account account = getCurrentAccount();
        model.addAttribute("account", account);
        return "dashboard";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        Optional<Account> existing = accountRepository.findByUsername(username);
        if (existing.isPresent()) {
            model.addAttribute("error", true);
            return "register";
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setBalance(5000.00); // Starting balance
        accountRepository.save(account);
        return "redirect:/?registered=true";
    }

    @GetMapping("/transactions")
    public String transactions(Model model) {
        Account account = getCurrentAccount();
        List<Transaction> transactions = transactionRepository.findByAccountOrderByTimestampDesc(account);
        model.addAttribute("transactions", transactions);
        return "transactions";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Double amount) {
        Account account = getCurrentAccount();
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType("Deposit");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Double amount, RedirectAttributes redirectAttributes) {
        Account account = getCurrentAccount();
        if (account.getBalance() < amount) {
            redirectAttributes.addFlashAttribute("error", "Insufficient balance!");
            return "redirect:/dashboard";
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType("Withdraw");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "redirect:/dashboard";
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String toUsername,
            @RequestParam Double amount,
            RedirectAttributes redirectAttributes) {
        Account sender = getCurrentAccount();
        if (sender.getUsername().equalsIgnoreCase(toUsername)) {
            redirectAttributes.addFlashAttribute("error", "Cannot transfer money to yourself!");
            return "redirect:/dashboard";
        }
        if (sender.getBalance() < amount) {
            redirectAttributes.addFlashAttribute("error", "Insufficient balance!");
            return "redirect:/dashboard";
        }
        Optional<Account> recipientOpt = accountRepository.findByUsername(toUsername);
        if (recipientOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Recipient username not found!");
            return "redirect:/dashboard";
        }
        Account recipient = recipientOpt.get();

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(recipient);

        Transaction senderTx = new Transaction();
        senderTx.setAccount(sender);
        senderTx.setType("Transfer Out to " + toUsername);
        senderTx.setAmount(amount);
        senderTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(senderTx);

        Transaction recipientTx = new Transaction();
        recipientTx.setAccount(recipient);
        recipientTx.setType("Transfer In from " + sender.getUsername());
        recipientTx.setAmount(amount);
        recipientTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(recipientTx);

        return "redirect:/dashboard";
    }
}