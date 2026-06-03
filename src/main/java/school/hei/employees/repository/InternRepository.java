package school.hei.employees.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import school.hei.employees.model.Intern;

@Repository
public interface InternRepository
    extends JpaRepository<Intern, Integer>, JpaSpecificationExecutor<Intern> {

  List<Intern> findByManagerId(Integer managerId);
}
