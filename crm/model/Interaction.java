package crm.model;

import java.time.Instant;

public class Interaction {
  private Integer id;
  private Integer customerId;
  private Instant whenUtc;
  private String kind; // "Call", "Email", "Meeting"
  private String note;

  public Interaction() {}

  public Interaction(Integer id, Integer customerId, Instant whenUtc, String kind, String note) {
    this.id = id; this.customerId = customerId; this.whenUtc = whenUtc; this.kind = kind; this.note = note;
  }

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }

  public Integer getCustomerId() { return customerId; }
  public void setCustomerId(Integer customerId) { this.customerId = customerId; }

  public Instant getWhenUtc() { return whenUtc; }
  public void setWhenUtc(Instant whenUtc) { this.whenUtc = whenUtc; }

  public String getKind() { return kind; }
  public void setKind(String kind) { this.kind = kind; }

  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }
}
