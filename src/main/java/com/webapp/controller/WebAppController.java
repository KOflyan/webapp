package com.webapp.controller;

import com.webapp.dao.entity.CustomerEntity;
import com.webapp.dto.WebAppDto;
import com.webapp.service.CustomerRegistrationService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Valid;
import java.util.List;


/** Server actions' controller. */
@Controller
@EnableAutoConfiguration
public class WebAppController extends WebMvcConfigurerAdapter {

    private final Logger logger = Logger.getLogger(WebAppController.class);

    /** DI. */
    @Autowired
    private CustomerRegistrationService service;

    /** View controllers for pages. */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/success").setViewName("success");
        registry.addViewController("/users").setViewName("users");
        registry.addViewController("/modify").setViewName("modify");
    }

    /** Initial state of form page. */
    @GetMapping("/")
    public String showForm(WebAppDto webAppDto) {
        return "form";
    }

    /** Post state. */
    @PostMapping("/")
    public String showResult(@Valid WebAppDto webAppDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        service.addCustomer(webAppDto.getFirstName(), webAppDto.getLastName(), webAppDto.getDateOfBirth(),
                webAppDto.getUsername(), webAppDto.getPassword());
        logger.info("Customer added to the DB: " + webAppDto);
        return "redirect:/success";
    }

    /** Initial (and the only one) state of the 'users' page. */
    @GetMapping("/users")
    public String showTable(Model model) {
        model.addAttribute("allCustomers", service.getAllCustomers());
        logger.info("Customers' list loaded");
        return "users";
    }

    /** Initial state of modify page. */
    @GetMapping("/modify/{id}")
    public String showModifyPage(@PathVariable(value = "id") String id, WebAppDto webAppDto, Model model) {
        logger.info("We get here!");
        model.addAttribute("customer", service.getCustomerById(id));
        return "modify";
    }

    @GetMapping("/modify/delete/{id}")
    public String deleteCustomer(@PathVariable(value = "id") String id) {
        CustomerEntity c = service.getCustomerById(id);
        service.deleteCustomer(c);
        logger.info("Customer " + c + " was deleted from the DB");
        return "redirect:/users";
    }

    // FIXME
    @PostMapping("/modify/{id}")
    public String editCustomerData(@PathVariable(value = "id") String id, @Valid WebAppDto webAppDto,
                                   BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            logger.info(webAppDto);
            return "redirect:/modify/{id}";
        }
        CustomerEntity c = service.getCustomerById(id);

        service.updateData(id, webAppDto.getFirstName(), webAppDto.getLastName(), webAppDto.getDateOfBirth(),
                webAppDto.getUsername(), webAppDto.getPassword());
        logger.info("Customer's " + c + " data was updated");
        return "redirect:/users";
    }

}
