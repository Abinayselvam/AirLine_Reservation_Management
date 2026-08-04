package examples.util;

import org.mindrot.jbcrypt.BCrypt;
    public class PasswordUtil
    {
        public static String encrypt(String password) {

            return BCrypt.hashpw(password, BCrypt.gensalt());
        }

        public static boolean verify(String password,String hash) {

            return BCrypt.checkpw(password, hash);
        }
    }