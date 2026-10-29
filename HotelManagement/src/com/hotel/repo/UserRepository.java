package com.hotel.repo;

import com.hotel.models.DeliveryPartner;
import com.hotel.models.User;
import com.hotel.models.UserRole;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private final Map<Integer, User> usersById = new HashMap<>();

    public void save(User user) {
        usersById.put(user.getUserId(), user);
    }

    public User findById(int id) {
        return usersById.get(id);
    }

    public User findByPhone(String phone) {
        for (User u : usersById.values()) {
            if (u.getPhone().equalsIgnoreCase(phone)) {
                return u;
            }
        }
        return null;
    }

    public User findByEmail(String email) {
        for (User u : usersById.values()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    public List<User> findByRole(UserRole role) {
        List<User> list = new ArrayList<>();
        for (User u : usersById.values()) {
            if (u.getRole() == role) {
                list.add(u);
            }
        }
        return list;
    }

    public List<DeliveryPartner> findPartnersByCity(String city) {
        List<DeliveryPartner> list = new ArrayList<>();
        for (User u : usersById.values()) {
            if (u instanceof DeliveryPartner) {
                DeliveryPartner dp = (DeliveryPartner) u;
                if (dp.getCity().equalsIgnoreCase(city)) {
                    list.add(dp);
                }
            }
        }
        return list;
    }
}
