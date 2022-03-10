package com.utd_bank.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.utd_bank.domain.enumeration.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name="tbl_role")
@Entity
public class Role {
	
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Integer id;


@Enumerated(EnumType.STRING)
@Column(length=30,nullable = false)
private UserRole name;
	

@Override
	public String toString() {
		return "["+name+"]";
	}

}
