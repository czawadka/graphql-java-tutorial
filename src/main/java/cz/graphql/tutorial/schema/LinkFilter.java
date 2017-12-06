package cz.graphql.tutorial.schema;

public class LinkFilter {
    private String contains;

    public LinkFilter(String contains) {
        this.contains = contains;
    }

    public LinkFilter() {
        this(null);
    }

    public String getContains() {
        return contains;
    }

    public void setContains(String contains) {
        this.contains = contains;
    }
}
