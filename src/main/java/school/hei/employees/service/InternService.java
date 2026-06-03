package school.hei.employees.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.hei.employees.model.Employee;
import school.hei.employees.model.Intern;
import school.hei.employees.repository.EmployeeRepository;
import school.hei.employees.repository.InternRepository;

@Service
@AllArgsConstructor
public class InternService {

  private final InternRepository internRepository;
  private final EmployeeRepository employeeRepository;

  public Page<Intern> findAll(Specification<Intern> specification, Pageable pageable) {
    return internRepository.findAll(specification, pageable);
  }

  public List<Intern> findAll(Specification<Intern> specification) {
    return internRepository.findAll(specification);
  }

  public List<Intern> findAll() {
    return internRepository.findAll();
  }

  public List<Intern> findAllById(List<Integer> ids) {
    return internRepository.findAllById(ids);
  }

  public Intern findById(Integer id) {
    return internRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Intern not found with id: " + id));
  }

  @Transactional
  public Intern save(Intern intern) {
    validateManager(intern.getManagerId());
    if (intern.getEmail() == null) intern.setEmail("");
    if (intern.getDepartment() == null) intern.setDepartment("");
    if (intern.getStartDate() == null) intern.setStartDate(LocalDate.now());
    if (intern.getEndDate() == null) intern.setEndDate(LocalDate.now().plusMonths(3));
    return internRepository.save(intern);
  }

  @Transactional
  public Intern update(Integer id, Intern updated) {
    Intern existing = findById(id);
    if (updated.getManagerId() != null) {
      validateManager(updated.getManagerId());
    }
    if (updated.getFirstname() != null) existing.setFirstname(updated.getFirstname());
    if (updated.getLastname() != null) existing.setLastname(updated.getLastname());
    if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
    if (updated.getDepartment() != null) existing.setDepartment(updated.getDepartment());
    if (updated.getRemunerated() != null) existing.setRemunerated(updated.getRemunerated());
    if (updated.getSalary() != null) existing.setSalary(updated.getSalary());
    if (updated.getManagerId() != null) existing.setManagerId(updated.getManagerId());
    if (updated.getStartDate() != null) existing.setStartDate(updated.getStartDate());
    if (updated.getEndDate() != null) existing.setEndDate(updated.getEndDate());
    return internRepository.save(existing);
  }

  @Transactional
  public void deleteById(Integer id) {
    Intern intern = findById(id);
    internRepository.delete(intern);
  }

  private void validateManager(Integer managerId) {
    if (managerId == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Un manager est requis pour le stagiaire");
    }
    Employee manager =
        employeeRepository
            .findById(managerId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Impossible d'assigner un stagiaire à un manager inactif"));
    if (!Boolean.TRUE.equals(manager.getActive())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Impossible d'assigner un stagiaire à un manager inactif");
    }
  }
}
