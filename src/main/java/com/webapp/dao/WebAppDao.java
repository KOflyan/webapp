package com.webapp.dao;

import com.webapp.dao.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/** Data access object. */
@Repository
public class WebAppDao {

    /** Managing set of entities. */
    @PersistenceContext
    private EntityManager entityManager;

    /** Add customer to the database. */
    public void add(CustomerEntity c) {
        entityManager.persist(c);
    }

    /** Remove customer from database. */
    public void remove(CustomerEntity c) {
        if (entityManager.contains(c)) {
            entityManager.remove(c);
        }
    }

    /** Get all customers. */
    public List<CustomerEntity> getAllCustomers() {
        return entityManager.createQuery("select c from CustomerEntity c").getResultList();
    }

    /** Get customer by id. */
    public CustomerEntity getCustomer(long id) {
        String query = "select c from CustomerEntity c where c.id = :id";
        return (CustomerEntity) entityManager.createQuery(query)
                .setParameter("id", id)
                .getSingleResult();
    }

    /** Update the customer's parameters. */
    public void update(long id, String firstName, String lastName, String dateOfBirth,
                       String username, String password) {

        String query = "update CustomerEntity c set c.firstName = :firstName, c.lastName = :lastName, " +
                "c.dateOfBirth = :dateOfBirth, c.username = :username, c.password = :password where c.id = :id";
        entityManager.createQuery(query)
                .setParameter("id", id)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .setParameter("dateOfBirth", dateOfBirth)
                .setParameter("username", username)
                .setParameter("password", password)
                .executeUpdate();
    }
}
