package com.example.devops.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applicationNo;
	private Long studentId;
	private Long programCode;
	private Date startDate;
	private String status;
	private Double amountPaid;
	
	public Enrollment(Long applicationNo, Long studentId, Long programCode, Date startDate, String status,
			Double amountPaid) {
		this.applicationNo = applicationNo;
		this.studentId = studentId;
		this.programCode = programCode;
		this.startDate = startDate;
		this.status = status;
		this.amountPaid = amountPaid;
	}

	public Enrollment() {
		
	}

	public Long getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(Long applicationNo) {
		this.applicationNo = applicationNo;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public Long getProgramCode() {
		return programCode;
	}

	public void setProgramCode(Long programCode) {
		this.programCode = programCode;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}
	
	
}
