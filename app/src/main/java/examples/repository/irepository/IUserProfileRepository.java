package examples.repository.irepository;

import examples.model.UserProfile;


public interface IUserProfileRepository {

    boolean save(UserProfile profile);

    boolean update(UserProfile profile);

    UserProfile findByUserId(int userId);

}
