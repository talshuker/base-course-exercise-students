package iaf.ofek.hadracha.base_course.web_server.Data;

public interface Entity<T extends Entity> extends Cloneable {
    int getId();
    void setId(int id);
    T clone();
}
