package crm.model;

import java.time.LocalDate;

public class Customer {
  private Integer id;
  private String name;
  private String company;
  private String industry;
  private String email;
  private String phone;
  private LocalDate nextFollowUp;

  public Customer() {}

  public Customer(Integer id, String name, String company, String industry,
                  String email, String phone, LocalDate nextFollowUp) {
    this.id = id; this.name = name; this.company = company; this.industry = industry;
    this.email = email; this.phone = phone; this.nextFollowUp = nextFollowUp;
  }

  // getters/setters
  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getCompany() { return company; }
  public void setCompany(String company) { this.company = company; }

  public String getIndustry() { return industry; }
  public void setIndustry(String industry) { this.industry = industry; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public LocalDate getNextFollowUp() { return nextFollowUp; }
  public void setNextFollowUp(LocalDate nextFollowUp) { this.nextFollowUp = nextFollowUp; }
}
