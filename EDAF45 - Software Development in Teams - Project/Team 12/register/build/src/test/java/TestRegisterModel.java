import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import register.View;


public class TestRegisterModel {
    View obs;

    @BeforeEach
    public void setUp(){
        obs = new View();

    }

    @Test
    public void regTestWithPath(){
        obs.getRegisterModel().writeRegistration("1");
        obs.getRegisterModel().writeRegistration("2");
    }

    @Test
    public void regTestPreRegistration() {
        obs.getRegisterModel().writeLateRegistration("1", "12:06:00");
    }
}
