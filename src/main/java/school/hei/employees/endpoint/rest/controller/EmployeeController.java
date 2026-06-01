package school.hei.employees.endpoint.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import school.hei.employees.conf.JsonServerUtils;
import school.hei.employees.repository.model.Employee;
import school.hei.employees.service.EmployeeService;

@RestController
@AllArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<List<Employee>> list(
      @RequestParam(required = false) List<Integer> id, HttpServletRequest request) {

    // GET_MANY: repeated ?id= params
    if (id != null && id.size() > 1) {
      return ResponseEntity.ok(employeeService.findAllById(id));
    }

    Map<String, String> params = extractParams(request);
    Specification<Employee> spec = JsonServerUtils.searchFilter(params, Employee.class);

    // GET_LIST: paginated
    if (params.containsKey("_start") && params.containsKey("_end")) {
      Pageable pageable = JsonServerUtils.pageableFrom(params);
      Page<Employee> page = employeeService.findAll(spec, pageable);
      return JsonServerUtils.toResponse(page);
    }

    // GET_MANY_REFERENCE or GET_LIST without pagination: unpaginated filtered
    List<Employee> employees = employeeService.findAll(spec);
    return ResponseEntity.ok(employees);
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

  private static Map<String, String> extractParams(HttpServletRequest request) {
    Map<String, String> params = new HashMap<>();
    request
        .getParameterNames()
        .asIterator()
        .forEachRemaining(
            key -> {
              if (!"id".equals(key)) {
                params.put(key, request.getParameter(key));
              }
            });
    return params;
  }
}
