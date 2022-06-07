package main.data;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFilter {
    private ArrayList<String> names;
    private ArrayList<String> tags;

    private boolean fuzzySearchEnabled;

    public DatabaseFilter() {
        names = new ArrayList<String>();
        names = new ArrayList<String>();
        fuzzySearchEnabled = false;
    }

    public void addNameFilter(String name) {
        names.add(name);
    }

    public void addTagFilter(String tag) {
        tags.add(tag);
    }

    public void setFuzzySearchEnabled(boolean fuzzySearchEnabled) {
        this.fuzzySearchEnabled =  fuzzySearchEnabled;
    }

    public String[] getNameFilters() {
        return (String[])names.toArray(new String[names.size()]);
    }

    public String[] getTagFilters() {
        return (String[])tags.toArray(new String[names.size()]);
    }

    public boolean getFuzzySearchEnabled() {
        return fuzzySearchEnabled;
    }
}
