package Examples.model;

import javax.management.relation.Role;

public abstract class User {

    protected int userId;
    protected String name;
    protected String email;
    protected String phone;
    protected String password;
    protected String dateOfBirth;
    protected String passportNo;
    protected Role role;

    public User() {}

    public User(int userId, String name, String email,
                String phone, String password,
                String dateOfBirth,
                String passportNo,
                Role role) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.passportNo = passportNo;
        this.role = role;
    }

    public abstract void showDashboard();
}
