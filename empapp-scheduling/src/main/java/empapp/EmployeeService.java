package empapp;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    private TaskScheduler taskScheduler;

    public EmployeeService(EmployeeRepository employeeRepository, TaskScheduler taskScheduler) {
        this.employeeRepository = employeeRepository;
        this.taskScheduler = taskScheduler;
    }

    @Scheduled(fixedRate = 5000)
    public void logCount() {
        List<EmployeeDto> employees = listEmployees();
        System.out.println("Running declared scheduling, employees" + employees.size());

    }

    public EmployeeDto createEmployee(CreateEmployeeCommand command) {
        Employee employee = new Employee(command.getName());
        ModelMapper modelMapper = new ModelMapper();
        if (command.getAddresses() != null) {
            employee.addAddresses(command.getAddresses().stream().map(a -> modelMapper.map(a, Address.class)).collect(Collectors.toList()));
        }
        employeeRepository.save(employee);

        taskScheduler.scheduleAtFixedRate(() -> System.out.println(command.getName()), 5000);
        //taskScheduler.scheduleWithFixedDelay()
        //taskScheduler.schedule()
        //taskScheduler.schedule(new CronTrigger(""))

        return modelMapper.map(employee, EmployeeDto.class);
    }

    public List<EmployeeDto> listEmployees() {
        ModelMapper modelMapper = new ModelMapper();
        return employeeRepository.findAllWithAddresses().stream()
                .map(e -> modelMapper.map(e, EmployeeDto.class))
                .collect(Collectors.toList());
    }

    public EmployeeDto findEmployeeById(long id) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(employeeRepository.findByIdWithAddresses(id)
                        .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id)),
                EmployeeDto.class);
    }

    @Transactional
    public EmployeeDto updateEmployee(long id, UpdateEmployeeCommand command) {
        Employee employeeToModify = employeeRepository.getOne(id);
        employeeToModify.setName(command.getName());
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(employeeToModify, EmployeeDto.class);
    }

    public EmployeeDto deleteEmployee(long id) {
        var employee = employeeRepository.findByIdWithAddresses(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(employee, EmployeeDto.class);
    }
}
