package com.webapp.controller;

import com.webapp.dao.entity.CustomerEntity;
import com.webapp.dto.WebAppDto;
import com.webapp.service.CustomerRegistrationService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Valid;


/** Server actions' controller. */
@Controller
@EnableAutoConfiguration
public class WebAppController extends WebMvcConfigurerAdapter {

    /** Logger. */
    private final Logger logger = Logger.getLogger(WebAppController.class);

    /** DI. */
    @Autowired
    private CustomerRegistrationService service;

    /** View controllers for specific pages. */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/success").setViewName("success");
        registry.addViewController("/users").setViewName("users");
        registry.addViewController("/modify").setViewName("modify");
    }

    /** Initial state of form page.
     *  User enters data, which is saved to data transfer object.
     * */
    @GetMapping("/")
    public String showForm(WebAppDto webAppDto) {
        return "form";
    }

    /** Post state.
     *  When submitted, data is validated and either parsed further or discarded.
     *  If the data is OK, a new customer is added to the database.
     * */
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

    /** Initial (and the only one) state of the 'users' page.
     *  Appending all available customers into table.
     *  Displaying the table.
     */
    @GetMapping("/users")
    public String showTable(Model model) {
        model.addAttribute("allCustomers", service.getAllCustomers());
        logger.info("Customers' list loaded");
        return "users";
    }

    /** Initial state of modify page.
     *  Modify page is based on the chosen customer's data, which is automatically inserted into text fields.
     *  When the user changes some of the given data, the result is being saved to data transfer object.
     */
    @GetMapping("/modify/{id}")
    public String showModifyPage(@PathVariable(value = "id") String id, WebAppDto webAppDto, Model model) {
        logger.info("We get here!");
        model.addAttribute("customer", service.getCustomerById(id));
        return "modify";
    }

    /** Deleting the customer.
     *  When invoked, the method deletes specific customer from the database.
     */
    @GetMapping("/modify/delete/{id}")
    public String deleteCustomer(@PathVariable(value = "id") String id) {
        CustomerEntity c = service.getCustomerById(id);
        service.deleteCustomer(c);
        logger.info("Customer " + c + " was deleted from the DB");
        return "redirect:/users";
    }

    /** Post-state of modify page.
     *  When the data is submitted, the method validates if the input is correct.
     *  If it isn't, page refreshes and the old data is restored.
     *  If the data is correct, it is appended to the customer object and the entity is updated.
     *  After that, the method redirects user back to the page with table.
     */
    @PostMapping("/modify/{id}")
    public String editCustomerData(@PathVariable(value = "id") String id, @Valid WebAppDto webAppDto,
                                   BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            logger.info(webAppDto);
            return "redirect:/modify/{id}";
        }

        service.updateData(id, webAppDto.getFirstName(), webAppDto.getLastName(), webAppDto.getDateOfBirth(),
                webAppDto.getUsername(), webAppDto.getPassword());

        logger.info("Customer's " + service.getCustomerById(id) + " data was updated");
        return "redirect:/users";
    }

}
