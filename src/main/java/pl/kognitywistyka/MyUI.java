package pl.kognitywistyka;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    private VerticalLayout inner;

    public void replaceContents(Component component) {
        inner.removeAllComponents();
        inner.addComponent(component);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        final MenuBar menuBar = new MenuBar();
        MenuItem home = menuBar.addItem("Główna", VaadinIcons.HOME, c -> renderMainPage());
        MenuItem m1 = menuBar.addItem("Pomidor");
        m1.setCommand(c -> replaceContents(new Label("<span style='color: red'>POMIDOR</span>", ContentMode.HTML)));
        MenuItem m2 = menuBar.addItem("Ogórek");
        m2.setCommand(c -> replaceContents(new Label("<span style='color: green'>OGÓREK</span>", ContentMode.HTML)));

        inner = new VerticalLayout();

        renderMainPage();
        layout.addComponents(menuBar, inner);
        
        setContent(layout);
        addStyleName("pink_website");
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

        inner.addComponents(name, button);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
