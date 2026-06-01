package school.hei.employees.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.hei.employees.repository.EmployeeRepository;
import school.hei.employees.repository.InternRepository;
import school.hei.employees.repository.model.Employee;
import school.hei.employees.repository.model.Intern;

@Service
@AllArgsConstructor
public class InternService {

  private final InternRepository internRepository;
  private final EmployeeRepository employeeRepository;

  public Page<Intern> findAll(Specification<Intern> specification, Pageable pageable) {
    return internRepository.findAll(specification, pageable);
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
    return internRepository.save(intern);
  }

  @Transactional
  public Intern update(Integer id, Intern updated) {
    Intern existing = findById(id);
    validateManager(updated.getManagerId());
    existing.setFirstname(updated.getFirstname());
    existing.setLastname(updated.getLastname());
    existing.setEmail(updated.getEmail());
    existing.setDepartment(updated.getDepartment());
    existing.setRemunerated(updated.getRemunerated());
    existing.setSalary(updated.getSalary());
    existing.setManagerId(updated.getManagerId());
    existing.setStartDate(updated.getStartDate());
    existing.setEndDate(updated.getEndDate());
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
          HttpStatus.BAD_REQUEST,
          "Impossible d'assigner un stagiaire à un manager inactif");
    }
  }
}
