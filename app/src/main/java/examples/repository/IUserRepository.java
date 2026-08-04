package examples.repository;

import examples.model.User;

import java.util.List;
public interface IUserRepository {

    boolean save(User user);

    User login(String email, String password);

    User findByEmail(String email);

    boolean update(User user);

    boolean delete(int id);

    List<User> findAll();

    boolean updatePassword(String email, String password);
}