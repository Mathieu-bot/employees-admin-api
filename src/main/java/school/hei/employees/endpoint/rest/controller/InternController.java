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
import school.hei.employees.repository.model.Intern;
import school.hei.employees.service.InternService;

@RestController
@AllArgsConstructor
@RequestMapping("/interns")
public class InternController {

  private final InternService internService;

  @GetMapping
  public ResponseEntity<List<Intern>> list(
      @RequestParam(required = false) List<Integer> id, HttpServletRequest request) {

    // GET_MANY: repeated ?id= params
    if (id != null && id.size() > 1) {
      return ResponseEntity.ok(internService.findAllById(id));
    }

    Map<String, String> params = extractParams(request);
    Specification<Intern> spec = JsonServerUtils.searchFilter(params, Intern.class);

    // GET_LIST: paginated
    if (params.containsKey("_start") && params.containsKey("_end")) {
      Pageable pageable = JsonServerUtils.pageableFrom(params);
      Page<Intern> page = internService.findAll(spec, pageable);
      return JsonServerUtils.toResponse(page);
    }

    // GET_MANY_REFERENCE or GET_LIST without pagination: unpaginated filtered
    List<Intern> interns = internService.findAll(spec);
    return ResponseEntity.ok(interns);
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
