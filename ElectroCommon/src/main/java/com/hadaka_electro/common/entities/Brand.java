package com.hadaka_electro.common.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = "categories")
@Table(name = "brands")
public class Brand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, unique = true)
	@Size(min = 4, message = "The minimum Name length is 4")
	@NotBlank(message = "The Name can not be blank")
	private String name;

	@Column(nullable = false)
	private String logo;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "brands_categories", joinColumns = @JoinColumn(name = "brand_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories = new HashSet<>();

	public Brand() {
	}

	public Brand(String name, String logo) {
		this.name = name;
		this.logo = logo;
	}
	public Brand(int id ,String name, String logo) {
		this.id=id;
		this.name = name;
		this.logo = logo;
	}

	public Brand(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getLogoPath() {
		if (logo == null || id == 0) {
			return "/assets/images/default-brand.png";
		}
		return "../brand_logos/" + this.id + "/" + logo;
	}
}
