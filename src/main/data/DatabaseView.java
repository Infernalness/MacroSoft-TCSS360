package main.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseView {
    private Database database;
    private DatabaseFilter filter;

    private ArrayList<Item> items;

    public DatabaseView(Database database, DatabaseFilter filter) {
        this.database = database;
        this.filter = filter;
    }

    private void retrieve() {
        items = new ArrayList<Item>(Arrays.asList(database.getItems()));

        if (filter.getFuzzySearchEnabled())
            filterByName();
        else
            filterByNameFuzzy();

        filterByTags();
    }

    private void filterByName() {
        String[] nameFilters = filter.getNameFilters();
        if (items.isEmpty())
            return;

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            for (String str : nameFilters) {
                if (item.getName().equalsIgnoreCase(str) == false) {
                    items.remove(i);
                    i = 0;
                    continue;
                }
            }
        }
    }

    private void filterByNameFuzzy() {
        String[] nameFilters = filter.getNameFilters();
        if (items.isEmpty())
            return;

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            for (String str : nameFilters) {
                if (
                        item.getName().equalsIgnoreCase(str) == false
                        && item.getName().contains(str) == false
                ) {
                    items.remove(i);
                    i = 0;
                    continue;
                }
            }
        }
    }

    private void filterByTags() {
        String[] tagFilters = filter.getTagFilters();
        if (items.isEmpty())
            return;

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            for (String str : tagFilters) {
                if (item.hasTag(str) == false) {
                    items.remove(i);
                    i = 0;
                    continue;
                }
            }
        }
    }

    public Item[] getItems() {
        retrieve();

        return (Item[])items.toArray(new Item[items.size()]);
    }
}
