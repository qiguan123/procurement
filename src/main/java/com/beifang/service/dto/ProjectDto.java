package com.beifang.service.dto;

public class ProjectDto {
	private Long id;
	private String name;
	private String code;
	private Long cfrsId;

	private String cfrsName;
	public String getCfrsName() {
		return cfrsName;
	}

	public void setCfrsName(String cfrsName) {
		this.cfrsName = cfrsName;
	}

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
