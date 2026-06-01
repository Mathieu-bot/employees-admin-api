package school.hei.employees.conf;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class JsonServerUtils {

  private static final List<String> PAGINATION_PARAMS = List.of("_sort", "_order", "_start", "_end");
  private static final List<String> IGNORED_PARAMS = List.of("q");

  public static <T> Specification<T> filterFrom(Map<String, String> allParams, Class<T> entityClass) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      for (Map.Entry<String, String> entry : allParams.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();

        if (PAGINATION_PARAMS.contains(key) || IGNORED_PARAMS.contains(key)) {
          continue;
        }

        if (value == null || value.trim().isEmpty()) {
          continue;
        }

        predicates.add(criteriaBuilder.equal(root.get(key), value));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static <T> Specification<T> searchFilter(Map<String, String> allParams, Class<T> entityClass) {
    Specification<T> filterSpec = filterFrom(allParams, entityClass);
    String q = allParams.get("q");

    if (q != null && !q.trim().isEmpty()) {
      String pattern = "%" + q.trim().toLowerCase() + "%";
      Specification<T> searchSpec =
          (root, query, criteriaBuilder) ->
              criteriaBuilder.or(
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("firstname")), pattern),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), pattern),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern));
      return filterSpec.and(searchSpec);
    }

    return filterSpec;
  }

  public static Pageable pageableFrom(Map<String, String> allParams) {
    int page = 0;
    int size = 10;
    Sort sort = Sort.unsorted();

    if (allParams.containsKey("_start") && allParams.containsKey("_end")) {
      int start = Integer.parseInt(allParams.get("_start"));
      int end = Integer.parseInt(allParams.get("_end"));
      size = end - start;
      page = size > 0 ? start / size : 0;
    }

    if (allParams.containsKey("_sort")) {
      String sortField = allParams.get("_sort");
      String order = allParams.getOrDefault("_order", "asc");
      Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
      sort = Sort.by(direction, sortField);
    }

    return PageRequest.of(page, size, sort);
  }

  public static <T> ResponseEntity<List<T>> toResponse(Page<T> page) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
