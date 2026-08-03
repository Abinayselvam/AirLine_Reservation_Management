package Examples.repository;

import Examples.model.User;

import java.util.List;

public interface UserRepository {

    boolean save(User user);

    User login(String email,String password);

    User findByEmail(String email);

    boolean update(User user);

    boolean delete(int userId);

    List<User> findAll();
}