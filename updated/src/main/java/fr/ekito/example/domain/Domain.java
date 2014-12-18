package fr.ekito.example.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * A Domain.
 */
@Document(collection = "T_DOMAIN")
public class Domain implements Serializable {

    @Id
    private String id;

    @Field("name")
    private String name;

    public Domain() {
    }

    public Domain(String n) {
        name = n;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Domain domain = (Domain) o;

        if (id != null ? !id.equals(domain.id) : domain.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "id=" + id +
                ", name='" + name + "'" +
                '}';
    }
}
