package com.example.devops.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Program {
	
	@Id
	private Long programCode;
	private String programName;
	private int duration;
	private double fee;

	public Program(Long programCode, String programName, int duration, double fee) {
		super();
		this.programCode = programCode;
		this.programName = programName;
		this.duration = duration;
		this.fee = fee;
	}

	public Program() {

	}

	public Long getProgramCode() {
		return programCode;
	}

	public void setProgramCode(Long programCode) {
		this.programCode = programCode;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}
	

}
