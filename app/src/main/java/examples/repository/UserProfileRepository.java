package examples.repository;

import examples.enums.CommunicationPreference;
import examples.enums.MealPreference;
import examples.enums.SeatPreference;
import examples.model.UserProfile;
import examples.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class UserProfileRepository implements IUserProfileRepository {
    @Override
    public boolean save(UserProfile profile)
    {
        String sql="""
        INSERT INTO user_profile
        (
        user_id,
        meal_preference,
        seat_preference,
        special_assistance,
        communication_preference,
        emergency_name,
        emergency_phone
        )
        VALUES(?,?,?,?,?,?,?)
        """;

        try(
                Connection con= DBConnection.getConnection();

                PreparedStatement ps=
                        con.prepareStatement(sql))
        {

            ps.setInt(1,profile.getUserId());

            ps.setString(2,
                    profile.getMealPreference().name());

            ps.setString(3,
                    profile.getSeatPreference().name());

            ps.setString(4,
                    profile.getSpecialAssistance());

            ps.setString(5,
                    profile.getCommunicationPreference().name());

            ps.setString(6,
                    profile.getEmergencyName());

            ps.setString(7,
                    profile.getEmergencyPhone());

            return ps.executeUpdate()>0;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public UserProfile findByUserId(int userId) {

        String sql =
                "SELECT * FROM user_profile WHERE user_id=?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                UserProfile profile = new UserProfile();

                profile.setProfileId(rs.getInt("profile_id"));
                profile.setUserId(rs.getInt("user_id"));
                profile.setMealPreference(MealPreference.valueOf(rs.getString("meal_preference")));
                profile.setSeatPreference(SeatPreference.valueOf( rs.getString("seat_preference")));
                profile.setSpecialAssistance(rs.getString("special_assistance"));
                profile.setCommunicationPreference(CommunicationPreference.valueOf( rs.getString("communication_preference")));
                profile.setEmergencyName(rs.getString("emergency_name"));
                profile.setEmergencyPhone(rs.getString("emergency_phone"));

                return profile;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean update(UserProfile profile) {

        String sql = """
            UPDATE user_profile
            SET meal_preference=?,
                seat_preference=?,
                special_assistance=?,
                communication_preference=?,
                emergency_name=?,
                emergency_phone=?
            WHERE user_id=?
            """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, profile.getMealPreference().name());
            ps.setString(2, profile.getSeatPreference().name());
            ps.setString(3, profile.getSpecialAssistance());
            ps.setString(4, profile.getCommunicationPreference().name());
            ps.setString(5, profile.getEmergencyName());
            ps.setString(6, profile.getEmergencyPhone());
            ps.setInt(7, profile.getUserId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

}
