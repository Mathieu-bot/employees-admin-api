package school.hei.employees.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "intern")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Intern extends User {

  private Boolean remunerated;

  private Integer salary;

  @Column(name = "manager_id")
  private Integer managerId;

  private LocalDate startDate;

  private LocalDate endDate;
}
