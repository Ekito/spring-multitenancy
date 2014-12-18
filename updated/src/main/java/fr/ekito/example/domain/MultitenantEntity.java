package fr.ekito.example.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * Created by Arnaud on 18/12/2014.
 */
public abstract class MultitenantEntity {

    @NotNull
    @DBRef
    Domain userDomain;

    public Domain getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(Domain userDomain) {
        this.userDomain = userDomain;
    }
}
