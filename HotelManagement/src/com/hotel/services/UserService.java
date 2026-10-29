package com.hotel.services;

import com.hotel.models.Address;
import com.hotel.models.Admin;
import com.hotel.models.Customer;
import com.hotel.models.DeliveryPartner;
import com.hotel.models.RestaurantOwner;
import com.hotel.models.User;
import com.hotel.models.UserRole;
import com.hotel.repo.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private int nextUserId = 1;
    private int nextAddressId = 1;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserService() {
        this(new UserRepository());
    }

    public UserRepository getRepository() {
        return userRepository;
    }

    public Customer registerCustomer(String name, String phone, String email) {
        validateUniqueUser(phone, email);
        Customer customer = new Customer(nextUserId++, name, phone, email);
        userRepository.save(customer);
        return customer;
    }

    public Address addCustomerAddress(int customerId, String city, String addressLine, String postalCode) {
        User user = userRepository.findById(customerId);
        if (user == null || !(user instanceof Customer)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        Customer customer = (Customer) user;
        Address address = new Address(nextAddressId++, customerId, city, addressLine, postalCode);
        customer.addAddress(address);
        return address;
    }

    public RestaurantOwner registerOwner(String name, String phone, String email) {
        validateUniqueUser(phone, email);
        RestaurantOwner owner = new RestaurantOwner(nextUserId++, name, phone, email);
        userRepository.save(owner);
        return owner;
    }

    public DeliveryPartner registerDeliveryPartner(String name, String phone, String email, String city) {
        validateUniqueUser(phone, email);
        DeliveryPartner partner = new DeliveryPartner(nextUserId++, name, phone, email, city);
        userRepository.save(partner);
        return partner;
    }

    public Admin registerAdmin(String name, String phone, String email) {
        validateUniqueUser(phone, email);
        Admin admin = new Admin(nextUserId++, name, phone, email);
        userRepository.save(admin);
        return admin;
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<DeliveryPartner> getPartnersByCity(String city) {
        return userRepository.findPartnersByCity(city);
    }

    private void validateUniqueUser(String phone, String email) {
        if (userRepository.findByPhone(phone) != null) {
            throw new IllegalArgumentException("Phone number '" + phone + "' is already registered.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email '" + email + "' is already registered.");
        }
    }
}
