package school.hei.employees.service;

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
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final InternRepository internRepository;

  public Page<Employee> findAll(Specification<Employee> specification, Pageable pageable) {
    return employeeRepository.findAll(specification, pageable);
  }

  public List<Employee> findAll(Specification<Employee> specification) {
    return employeeRepository.findAll(specification);
  }

  public List<Employee> findAll() {
    return employeeRepository.findAll();
  }

  public List<Employee> findAllById(List<Integer> ids) {
    return employeeRepository.findAllById(ids);
  }

  public Employee findById(Integer id) {
    return employeeRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Employee not found with id: " + id));
  }

  @Transactional
  public Employee save(Employee employee) {
    return employeeRepository.save(employee);
  }

  @Transactional
  public Employee update(Integer id, Employee updated) {
    Employee existing = findById(id);
    boolean isBeingDeactivated =
        Boolean.FALSE.equals(updated.getActive()) && Boolean.TRUE.equals(existing.getActive());
    if (isBeingDeactivated) {
      List<Intern> interns = internRepository.findByManagerId(id);
      if (!interns.isEmpty()) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "Impossible de désactiver un employé qui a encore des stagiaires");
      }
    }
    if (updated.getFirstname() != null) existing.setFirstname(updated.getFirstname());
    if (updated.getLastname() != null) existing.setLastname(updated.getLastname());
    if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
    if (updated.getDepartment() != null) existing.setDepartment(updated.getDepartment());
    if (updated.getSalary() != null) existing.setSalary(updated.getSalary());
    if (updated.getActive() != null) existing.setActive(updated.getActive());
    return employeeRepository.save(existing);
  }

  @Transactional
  public void deleteById(Integer id) {
    Employee employee = findById(id);
    List<Intern> interns = internRepository.findByManagerId(id);
    if (!interns.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Impossible de supprimer un employé qui encadre encore des stagiaires");
    }
    employeeRepository.delete(employee);
  }
}
