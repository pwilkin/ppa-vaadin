package pl.kognitywistyka;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.Query;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import pl.kognitywistyka.data.User;
import pl.kognitywistyka.database.DatabaseConnection;
import pl.kognitywistyka.security.SecurityUtil;

/**
 * Created by pwilkin on 06-Jun-19.
 */
public class LoginTest extends CustomComponent {

    public LoginTest() {

        TextField textField = new TextField("Użytkownik: ");
        PasswordField passwordField = new PasswordField("Hasło: ");
        Button tester = new Button("Testuj hasło");
        tester.addClickListener(cl -> testPasswords(textField.getValue(), passwordField.getValue()));
        FormLayout fl = new FormLayout();
        fl.addComponents(textField, passwordField, tester);
        setCompositionRoot(fl);
    }

    private void testPasswords(String user, String pass) {
        DatabaseConnection.getInstance().runInORM(em -> {
            Query query = em.createQuery("from User where userName = :user");
            query.setParameter("user", user);
            List<User> lst = query.getResultList();
            if (lst.size() > 0) {
                User userBean = lst.get(0);
                String pwh = userBean.getPasswordHash();
                byte[] pwhBytes = pwh.getBytes();
                byte[] salt = Arrays.copyOfRange(pwhBytes, 0, 16);
                byte[] digest = Arrays.copyOfRange(pwhBytes, 16, pwhBytes.length);
                String saltedPass = SecurityUtil.generatePasswordWithSalt(pass, salt);
                if (Objects.equals(saltedPass, pwh)) {
                    Notification.show("Hasło OK!", Type.WARNING_MESSAGE);
                } else {
                    Notification.show("Złe hasło!", Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Niepoprawny użytkownik!", Type.ERROR_MESSAGE);
            }
        });
    }

}
