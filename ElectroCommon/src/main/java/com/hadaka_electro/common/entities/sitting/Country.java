package com.hadaka_electro.common.entities.sitting;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter
@Setter
@ToString(exclude = {"states"})
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 5)
    private String code;

    @OneToMany(mappedBy = "country", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<State> states = new HashSet<>();

    public void addState(State state) {
        this.states.add(state);
        state.setCountry(this);
    }

    public void removeState(State state) {
        this.states.remove(state);
        state.setCountry(null);
    }
}
