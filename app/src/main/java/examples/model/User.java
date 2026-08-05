package examples.model;

import examples.enums.CommunicationPreference;
import examples.enums.Permission;
import java.util.Set;
import examples.enums.Role;

public abstract class User {

    protected int userId;
    protected String name;
    protected String email;
    protected String phone;
    protected String password;
    protected String dateOfBirth;
    protected String passportNo;
    protected examples.enums.Role role;
    protected boolean active;
    protected CommunicationPreference communicationPreference;

    public User() {}

    public User(int userId, String name, String email,
                String phone, String password,
                String dateOfBirth,
                String passportNo,
                Role role,CommunicationPreference communicationPreference ,boolean active) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.passportNo = passportNo;
        this.role = role;
        this.communicationPreference = communicationPreference;
        this.active=active;
    }


    public int getId()
    {
        return userId;
    }
    public String getName()
    {
        return name;
    }
    public String getEmail()
    {
        return email;
    }
    public String getPhone()
    {
        return phone;
    }
    public String getPassword()
    {
        return password;
    }
    public String getDateOfBirth()
    {
        return dateOfBirth;
    }
    public String getPassportNo()
    {
        return passportNo;
    }
    public examples.enums.Role getRole()
    {
        return role;
    }
    public CommunicationPreference getCommunicationPreference() {
        return communicationPreference;
    }
    public void setCommunicationPreference(
            CommunicationPreference communicationPreference) {

        this.communicationPreference = communicationPreference;
    }
    public boolean getActive()
    {
        return active;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setEmail(String email)
    {
        this.email  = email;
    }
    public void setPhone(String phone)
    {
        this.phone=phone;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public void setRole(examples.enums.Role role)
    {
        this.role = role;
    }
    public void setDateOfBirth(String dateOfBirth)
    {
        this.dateOfBirth =dateOfBirth;
    }
    public void setPassportNo(String passportNo)
    {
        this.passportNo=passportNo;
    }
    public void setUserId(int UserId)
    {
        this.userId = UserId;
    }
    public void setActive(boolean active)
    {
        this.active=active;
    }
    public abstract Set<Permission> getPermissions();

    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }
    public abstract void showDashboard();


}
