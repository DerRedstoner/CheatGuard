package de.derredstoner.anticheat.check.annotation;

import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {

    String name();
    String description();
    Category category();
    SubCategory subCategory();
    boolean experimental() default false;

}
