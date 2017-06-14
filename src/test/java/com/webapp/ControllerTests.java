package com.webapp;

import com.webapp.controller.WebAppController;
import com.webapp.dao.entity.CustomerEntity;
import com.webapp.service.CustomerRegistrationService;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(WebAppController.class)
public class ControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerRegistrationService service;

    @Autowired
    private WebAppController webAppController;

    /** Application is runnable. */
    @Test
    public void contextLoads() throws Exception {
        assertThat(webAppController).isNotNull();
    }


    /** Check if method invocation calls the file of valid type. */
    @Test
    public void checkhtmlType() throws Exception {
        this.mvc.perform(get("/"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
        this.mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
    }

    /** Test if url changes dynamically with the customer's id. */
    @Test
    public void checkUrlUpdate() throws Exception {
        String date = "12.03.1995";
        CustomerEntity c = new CustomerEntity("Sample", "Example", date, "user", "password");

        when(service.getCustomerById("1")).thenReturn(c);
        this.mvc.perform(get("/modify/1"))
                .andDo(print())
                .andExpect(status().isOk());
        c.setId(3);
        when(service.getCustomerById("3")).thenReturn(c);
        this.mvc.perform(get("/modify/3"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    /** Check if contents of customers' list are used when generating table of active users. */
    @Test
    public void checkList() throws Exception {
        String date = "12.03.1995";
        CustomerEntity c = new CustomerEntity("Sample", "Example", date, "user", "password");
        CustomerEntity c2 = new CustomerEntity("Sample2", "Example", date, "password", "user");
        List<CustomerEntity> test = new ArrayList<>(Arrays.asList(c, c2));

        when(service.getAllCustomers()).thenReturn(test);

        this.mvc.perform(get("/success"))
                .andDo(print())
                .andExpect(status().isOk());
        MvcResult r = this.mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String result = r.getResponse().getContentAsString();

        assert result.contains("Sample");
        assert result.contains("Sample2");
    }
}
