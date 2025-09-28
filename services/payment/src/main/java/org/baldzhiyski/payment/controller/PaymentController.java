package org.baldzhiyski.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Value("${spring.mail.host:localhost}")
    private String mailHost;

    @Value("${spring.mail.web-port:1080}")
    private int mailWebPort;

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String orderRef, Model model) {
        model.addAttribute("orderRef", orderRef);
        model.addAttribute("mailDevUrl", "http://" + mailHost + ":" + mailWebPort);
        return "payment-success"; // templates/payment-success.html
    }

    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam String orderRef, Model model) {
        model.addAttribute("orderRef", orderRef);
        model.addAttribute("mailDevUrl", "http://" + mailHost + ":" + mailWebPort);
        return "payment-cancel"; // templates/payment-cancel.html
    }
}
