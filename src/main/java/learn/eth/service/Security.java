package learn.eth.service;

import org.springframework.stereotype.Service;

/**
 *
 * simple security stub for now
 */
@Service
public class Security {


    public static final String  LOG_IN_SUCCESS= "Log In Ok";
    public static final String  LOG_IN_FAIL= "Log In Fail try again";
    public static final String  NOT_AUThORISED= "Please log in to use this sevice";

    private enum States {
        LOGGED_IN, LOGGED_OUT
    }

    private static final String  PASSWORD= "tmp123";

    private States state;

    public boolean login(String password) {
        if(password.equals(PASSWORD)) {
            state = States.LOGGED_IN;
            return true;
        } else return false;
    }

    public void logout() {
        state = States.LOGGED_OUT;
    }


    public boolean isLoggedIn() {

        if(state ==States.LOGGED_IN ) {
          return true;
        }

        return false;
    }

}
