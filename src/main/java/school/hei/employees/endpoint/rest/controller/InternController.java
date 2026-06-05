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
import school.hei.employees.model.Intern;
import school.hei.employees.service.InternService;

@RestController
@AllArgsConstructor
@RequestMapping("/interns")
public class InternController {

  private final InternService internService;

  @GetMapping
  public ResponseEntity<?> list(
      @RequestParam(required = false) List<Integer> id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String q,
      @RequestParam(name = "managerId", required = false) Integer managerId,
      @RequestParam(required = false) Boolean remunerated) {

    if (id != null && !id.isEmpty()) {
      return ResponseEntity.ok(internService.findAllById(id));
    }

    Specification<Intern> spec = searchByQ(q);

    if (managerId != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("managerId"), managerId));
    }

    if (remunerated != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("remunerated"), remunerated));
    }

    Sort springSort = parseSort(sort);
    Pageable pageable = PageRequest.of(page, pageSize, springSort);
    Page<Intern> result = internService.findAll(spec, pageable);

    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public Intern getById(@PathVariable Integer id) {
    return internService.findById(id);
  }

  @PostMapping
  public Intern create(@RequestBody Intern intern) {
    return internService.save(intern);
  }

  @PutMapping("/{id}")
  public Intern update(@PathVariable Integer id, @RequestBody Intern intern) {
    return internService.update(id, intern);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    internService.deleteById(id);
  }

  private Specification<Intern> searchByQ(String q) {
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
