package com.webapp.service;

import com.webapp.dao.WebAppDao;
import com.webapp.dao.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/** Service for managing requests. */
@Service
@Transactional
public class CustomerRegistrationService {

    /** DI */
    @Autowired
    private WebAppDao dao;


    /** Format given date to more pleasant pattern. */
    private String formatDate(Date birthDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(birthDate);
    }


    public List<CustomerEntity> getAllCustomers() {
        return dao.getAllCustomers();
    }

    public void addCustomer(String firstName, String lastName, Date birthDate, String username, String password) {
        CustomerEntity newCustomer = new CustomerEntity(firstName, lastName, formatDate(birthDate), username, password);
        dao.add(newCustomer);
    }

    public CustomerEntity getCustomerById(String id) {
        return dao.getCustomer(Long.parseLong(id));
    }

    public void deleteCustomer(CustomerEntity c) {
        dao.remove(c);
    }

    public void updateData(String id, String firstName, String lastName, Date dateOfBirth,
                           String username, String password) {
        dao.update(Long.parseLong(id), firstName, lastName, formatDate(dateOfBirth), username, password);
    }

}
