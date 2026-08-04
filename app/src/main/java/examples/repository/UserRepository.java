package examples.repository;

import examples.enums.Role;
import examples.model.Passenger;
import examples.model.User;
import examples.repository.irepository.IUserRepository;
import examples.util.DBConnection;
import examples.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class UserRepository implements IUserRepository {

    @Override
    public boolean save(User user) {

        String sql = """
                INSERT INTO users
                (
                name,
                email,
                phone,
                date_of_birth,
                passport_no,
                password,
                role,
                active
                )
                VALUES
                (?,?,?,?,?,?,?,?)
                """;

        try (

                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)

        ) {

            ps.setString(1,
                    user.getName());

            ps.setString(2,
                    user.getEmail());

            ps.setString(3,
                    user.getPhone());

            ps.setString(4,
                    user.getDateOfBirth());

            ps.setString(5,
                    user.getPassportNo());

            ps.setString(6,
                    user.getPassword());

            ps.setString(7,
                    user.getRole().name());

            ps.setBoolean(8,
                    true);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    @Override
    public User login(String email, String password) {

        User user = findByEmail(email);

        if (user == null) {
            return null;
        }

        if (PasswordUtil.verify(password, user.getPassword())) {
            return user;
        }

        return null;
    }


    @Override
    public User findByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email=? AND active=true";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Passenger passenger = new Passenger();

                passenger.setUserId(rs.getInt("user_id"));
                passenger.setName(rs.getString("name"));
                passenger.setEmail(rs.getString("email"));
                passenger.setPhone(rs.getString("phone"));
                passenger.setDateOfBirth(rs.getString("date_of_birth"));
                passenger.setPassportNo(rs.getString("passport_no"));
                passenger.setPassword(rs.getString("password"));
                passenger.setRole(Role.valueOf(rs.getString("role")));
                passenger.setActive(rs.getBoolean("active"));

                return passenger;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean update(User user) {

        String sql = """
            UPDATE users
            SET
            name=?,
            phone=?,
            date_of_birth=?,
            passport_no=?
            WHERE id=?
            """;

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, user.getName());

            ps.setString(2, user.getPhone());

            ps.setString(3, user.getDateOfBirth());

            ps.setString(4, user.getPassportNo());

            ps.setInt(5, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(int id) {

        String sql =
                "UPDATE users SET active=false WHERE id=?";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public boolean updatePassword(String email,
                                  String password) {

        String sql =
                "UPDATE users SET password=? WHERE email=?";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, password);

            ps.setString(2, email);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // Remaining methods will be implemented in the next step
}
