package school.hei.employees.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import school.hei.employees.model.Employee;

@Repository
public interface EmployeeRepository
    extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {}
