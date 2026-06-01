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

@Service
@AllArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final InternRepository internRepository;

  public Page<Employee> findAll(Specification<Employee> specification, Pageable pageable) {
    return employeeRepository.findAll(specification, pageable);
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
        .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
  }

  @Transactional
  public Employee save(Employee employee) {
    return employeeRepository.save(employee);
  }

  @Transactional
  public Employee update(Integer id, Employee updated) {
    Employee existing = findById(id);
    existing.setFirstname(updated.getFirstname());
    existing.setLastname(updated.getLastname());
    existing.setEmail(updated.getEmail());
    existing.setDepartment(updated.getDepartment());
    existing.setSalary(updated.getSalary());
    existing.setActive(updated.getActive());
    return employeeRepository.save(existing);
  }

  @Transactional
  public void deleteById(Integer id) {
    Employee employee = findById(id);
    employeeRepository.delete(employee);
  }

  @Transactional
  public Employee deactivate(Integer id) {
    Employee employee = findById(id);
    List<school.hei.employees.repository.model.Intern> interns =
        internRepository.findByManagerId(id);
    if (!interns.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Impossible de désactiver un employé qui a encore des stagiaires");
    }
    employee.setActive(false);
    return employeeRepository.save(employee);
  }
}
