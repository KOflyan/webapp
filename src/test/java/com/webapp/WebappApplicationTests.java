package com.webapp;

import com.webapp.service.CustomerRegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class WebappApplicationTests {

	@Autowired
    private TestRestTemplate restTemplate;

	@Autowired
    private CustomerRegistrationService service;

	@Test
	public void initialPage() {
		String body = this.restTemplate.getForObject("/", String.class);
		assertThat(body).contains("Registration form");
	}

	/** Test if the proper file corresponds to given url. */
	@Test
    public void tablePage() {
	    String body = this.restTemplate.getForObject("/users", String.class);
	    assertThat(body).contains("Database");
    }

    /** Add customer to the database. */
    @Before
    public void setup() {
        String date = "12.03.1995";
        Date d = new Date();
        try {
            d = new SimpleDateFormat("dd.MM.yyyy").parse(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        service.addCustomer("Sample", "Example", d, "user", "password");
    }

    /** Test if autogenerated id for the first customer is 1. */
    @Test
    public void testIfGeneratedIdCorrect() {
        this.restTemplate.getForEntity("/modify/{id}",
                String.class, "1");
        assertThat(this.service.getCustomerById("1").getFirstName()).isEqualTo("Sample");
    }
}