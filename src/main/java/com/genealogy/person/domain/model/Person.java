package com.genealogy.person.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class Person {

  private Long id;
  private String fullName;
  private Gender gender;
  private LocalDate dateOfBirth;
  private LocalDate dateOfDeath;

  private Person(
      Long id, String fullName, Gender gender, LocalDate dateOfBirth, LocalDate dateOfDeath) {

    validateName(fullName);
    validateDates(dateOfBirth, dateOfDeath);

    this.id = id;
    this.fullName = fullName;
    this.gender = Objects.requireNonNull(gender);
    this.dateOfBirth = dateOfBirth;
    this.dateOfDeath = dateOfDeath;
  }

  // 👉 Factory method (create mới)
  public static Person create(String fullName, Gender gender, LocalDate dateOfBirth) {

    return new Person(null, fullName, gender, dateOfBirth, null);
  }

  // 👉 Rehydrate từ DB
  public static Person restore(
      Long id, String fullName, Gender gender, LocalDate dob, LocalDate dod) {

    return new Person(id, fullName, gender, dob, dod);
  }

  // =========================
  // BUSINESS METHODS
  // =========================

  public void changeName(String newName) {
    validateName(newName);
    this.fullName = newName;
  }

  public void markAsDeceased(LocalDate dateOfDeath) {
    if (this.dateOfBirth != null && dateOfDeath.isBefore(this.dateOfBirth)) {
      throw new IllegalArgumentException("Death date before birth");
    }
    this.dateOfDeath = dateOfDeath;
  }

  public boolean isAlive() {
    return this.dateOfDeath == null;
  }

  // =========================
  // VALIDATION
  // =========================

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
  }

  private void validateDates(LocalDate dob, LocalDate dod) {
    if (dob != null && dod != null && dod.isBefore(dob)) {
      throw new IllegalArgumentException("Invalid birth/death dates");
    }
  }

  // =========================
  // GETTERS (read-only)
  // =========================

  public Long getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public Gender getGender() {
    return gender;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public LocalDate getDateOfDeath() {
    return dateOfDeath;
  }
}
