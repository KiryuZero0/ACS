package com.example.quoteserver.controller;

import com.example.quoteserver.model.Quote;
import com.example.quoteserver.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Controller
public class QuoteController {
    @Autowired
    private QuoteRepository quoteRepository;

    @GetMapping("/")
    public String homePage(Model model) {
        List<Quote> quotes = quoteRepository.findAll();
        Quote randomQuote = quotes.get(new Random().nextInt(quotes.size()));
        model.addAttribute("quote", randomQuote);
        return "index";
    }
}
