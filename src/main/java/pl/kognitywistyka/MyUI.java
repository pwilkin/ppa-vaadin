package pl.kognitywistyka;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import pl.kognitywistyka.data.User;
import pl.kognitywistyka.database.DatabaseConnection;
import pl.kognitywistyka.security.SecurityUtil;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET_XHR)
public class MyUI extends UI {

    public static final String AGREES_TO_COOKIES = "AGREES_TO_COOKIES";

    private VerticalLayout inner;

    public void replaceContents(Component component) {
        inner.removeAllComponents();
        inner.addComponent(component);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        boolean agreedToCookies = false;
        for (Cookie cookie : VaadinRequest.getCurrent().getCookies()) {
            if (AGREES_TO_COOKIES.equals(cookie.getName()) && "true".equals(cookie.getValue())) {
                agreedToCookies = true;
                break;
            }
        }

        final AbsoluteLayout al = new AbsoluteLayout();
        final VerticalLayout layout = new VerticalLayout();
        final MenuBar menuBar = new MenuBar();
        MenuItem home = menuBar.addItem("Główna", VaadinIcons.HOME, c -> renderMainPage());
        MenuItem m1 = menuBar.addItem("Pomidor");
        m1.setCommand(c -> replaceContents(new Label("<span style='color: red'>POMIDOR</span>", ContentMode.HTML)));
        MenuItem m2 = menuBar.addItem("Ogórek");
        m2.setCommand(c -> replaceContents(new Label("<span style='color: green'>OGÓREK</span>", ContentMode.HTML)));
        MenuItem lt = menuBar.addItem("Login test");
        lt.setCommand(c -> replaceContents(new LoginTest()));
        inner = new VerticalLayout();

        renderMainPage();
        layout.addComponents(menuBar, inner);
        al.addComponent(layout, "top: 0; left: 0");

        if (!agreedToCookies) {
            HorizontalLayout cookieMonster = new HorizontalLayout();
            cookieMonster.addComponent(new Label("Wyraź zgodę na ciasteczka!"));
            Button button = new Button("OMNOMNOMNOM");
            button.addClickListener(cl -> agreeToCookies(cookieMonster));
            cookieMonster.addComponent(button);
            cookieMonster.setWidth("100%");
            cookieMonster.addStyleName("cookie_monster");
            al.addComponent(cookieMonster, "bottom: 0; left: 0");
        }
        
        setContent(al);
        addStyleName("pink_website");
    }

    private void agreeToCookies(HorizontalLayout cookieMonster) {
        Cookie cookie = new Cookie(AGREES_TO_COOKIES, "true");
        cookie.setPath("/");
        cookie.setMaxAge(999999);
        VaadinResponse.getCurrent().addCookie(cookie);
        cookieMonster.setVisible(false);
    }

    private void renderMainPage() {
        inner.removeAllComponents();
        final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener(e -> {
            inner.removeAllComponents();
            inner.addComponentsAndExpand(new NameTable(name.getValue()));
        });
        button.addStyleName("green_button");

        Integer[] count = new Integer[] { null };

        DatabaseConnection.getInstance().runInORM(em -> {
            List<User> users = em.createQuery("from User", User.class).getResultList();
            count[0] = users.size();
        });

        inner.addComponents(name, button);

        if (count[0] == 0) {
            Button addRoot = new Button("Dodaj użytkownika admin z hasłem admin");
            addRoot.addClickListener(cl -> {
                DatabaseConnection.getInstance().runInORM(em -> {
                    em.getTransaction().begin();
                    User user = new User();
                    user.setUserName("admin");
                    user.setUserRole("admin");
                    user.setPasswordHash(SecurityUtil.generatePasswordHash("admin"));
                    em.persist(user);
                    em.getTransaction().commit();
                });
                renderMainPage();
            });
            inner.addComponents(addRoot);
        } else {
            inner.addComponents(new Label("Użytkownicy: " + count[0]));
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
