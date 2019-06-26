package com.beifang.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Project {
	
	@Id @GeneratedValue
	private Long id;
	private String name;
	private String code;
	
	private Long cfrsId;
	
	
	public Long getCfrsId() {
		return cfrsId;
	}

	public void setCfrsId(Long cfrsId) {
		this.cfrsId = cfrsId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
