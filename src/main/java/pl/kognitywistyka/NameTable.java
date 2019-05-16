package pl.kognitywistyka;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by pwilkin on 16-May-19.
 */
public class NameTable extends CustomComponent {

    public class NameEntry {
        protected Integer num;
        protected String name;

        public NameEntry(Integer num, String name) {
            this.num = num;
            this.name = name;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public NameTable(String name) {
        List<NameEntry> nameEntries = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            NameEntry ne = new NameEntry(i, name);
            nameEntries.add(ne);
        }
        VerticalLayout vl = new VerticalLayout();
        Grid<NameEntry> grid = new Grid<>();
        grid.setItems(nameEntries);
        Column<NameEntry, Integer> col = grid.addColumn(NameEntry::getNum);
        col.setCaption("Liczba porządkowa");
        Column<NameEntry, String> col2 = grid.addColumn(NameEntry::getName);
        col2.setCaption("Imię i nazwisko");
        Button zniszczWszystko = new Button("Zniszcz wszystko!");
        zniszczWszystko.addClickListener(e -> ((MyUI) getUI()).replaceContents(new Label("<span style='font-size: 100px'>BUM!</span>", ContentMode.HTML)));
        vl.addComponents(grid, zniszczWszystko);
        setCompositionRoot(vl);
    }

}
