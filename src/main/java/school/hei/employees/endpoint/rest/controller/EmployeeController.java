package school.hei.employees.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.hei.employees.model.Employee;
import school.hei.employees.service.EmployeeService;

@RestController
@AllArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<?> list(
      @RequestParam(required = false) List<Integer> id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Boolean active,
      @RequestParam(required = false) String department) {

    if (id != null && !id.isEmpty()) {
      return ResponseEntity.ok(employeeService.findAllById(id));
    }

    Specification<Employee> spec = searchByQ(q);

    if (active != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
    }

    if (department != null && !department.isEmpty()) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("department"), department));
    }

    Sort springSort = parseSort(sort);
    Pageable pageable = PageRequest.of(page, pageSize, springSort);
    Page<Employee> result = employeeService.findAll(spec, pageable);

    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public Employee getById(@PathVariable Integer id) {
    return employeeService.findById(id);
  }

  @PostMapping
  public Employee create(@RequestBody Employee employee) {
    return employeeService.save(employee);
  }

  @PutMapping("/{id}")
  public Employee update(@PathVariable Integer id, @RequestBody Employee employee) {
    return employeeService.update(id, employee);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    employeeService.deleteById(id);
  }

  private Specification<Employee> searchByQ(String q) {
    return (root, query, cb) -> {
      if (q == null || q.trim().isEmpty()) return cb.conjunction();
      String pattern = "%" + q.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("firstname")), pattern),
          cb.like(cb.lower(root.get("lastname")), pattern),
          cb.like(cb.lower(root.get("email")), pattern));
    };
  }

  private Sort parseSort(String sort) {
    if (sort == null || sort.isBlank()) return Sort.unsorted();
    String[] parts = sort.split(",");
    return Sort.by(
        parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
            ? Sort.Direction.DESC
            : Sort.Direction.ASC,
        parts[0]);
  }
}
