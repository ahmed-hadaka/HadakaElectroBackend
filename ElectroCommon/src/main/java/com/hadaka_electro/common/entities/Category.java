package com.hadaka_electro.common.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"parent", "children"})
@Table(name = "categories")
@EqualsAndHashCode(exclude = {"parent", "id", "children"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @Size(min = 2, message = "The minimum Name length is 2")
    @NotBlank(message = "The Name can not be blank")
    private String name;

    @Column(nullable = false, unique = true)
    @Size(min = 2, message = "The minimum Alias length is 2")
    @NotBlank(message = "The Alias can not be blank")
    private String alias;

    @Column(nullable = false)
    private String image;

    private boolean enabled;

    @Column(name = "parent_ids")
    private String parentIds;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Category> children = new HashSet<>();

    public Category() {

    }

    public Category(String name, String alias, String image) {
        this.name = name;
        this.alias = alias;
        this.image = image;
    }

    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        image = ""; // can't be null
    }

    public String getImagePath() {
        if (image == null || id == 0)
            return "/assets/images/default-category.png";

        return "../category_images/" + id + "/" + image;
    }

    public void addChild(Category category) {
        children.add(category);
        category.setParent(this);
    }

    public void addAllChildren(Collection<? extends Category> categories) {
        children.addAll(categories);
        for (Category category : categories) {
            category.setParent(this);
        }
    }

}
