package com.employees.employeemanagement.controller;

import com.employees.employeemanagement.dto.EmployeeDTO;
import com.employees.employeemanagement.entity.Employee;
import com.employees.employeemanagement.exception.EmployeeNotFoundException;
import com.employees.employeemanagement.exception.GlobalExceptionHandler;
import com.employees.employeemanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private final EmployeeService employeeService =
            mock(EmployeeService.class);

    private final EmployeeController employeeController =
            new EmployeeController(employeeService);

    private final MockMvc mockMvc =
            MockMvcBuilders
                    .standaloneSetup(employeeController)
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();

    private final ObjectMapper objectMapper =
            new ObjectMapper();


    // =========================================================
    // TEST 1: GET ALL EMPLOYEES
    // =========================================================

    @Test
    void getAllEmployees_shouldReturnEmployees() throws Exception {

        EmployeeDTO employee = new EmployeeDTO();

        employee.setId(1);
        employee.setName("Janu");
        employee.setSalary(65000);

        when(employeeService.getAllEmployees())
                .thenReturn(List.of(employee));

        mockMvc.perform(
                        get("/employees/all")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Janu"))
                .andExpect(jsonPath("$[0].salary").value(65000));

        verify(employeeService)
                .getAllEmployees();
    }


    // =========================================================
    // TEST 2: GET EMPLOYEE BY ID
    // =========================================================

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {

        EmployeeDTO employee = new EmployeeDTO();

        employee.setId(1);
        employee.setName("Janu");
        employee.setSalary(65000);

        when(employeeService.getEmployeeById(1))
                .thenReturn(employee);

        mockMvc.perform(
                        get("/employees/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Janu"))
                .andExpect(jsonPath("$.salary").value(65000));

        verify(employeeService)
                .getEmployeeById(1);
    }


    // =========================================================
    // TEST 3: GET EMPLOYEE BY ID - NOT FOUND
    // =========================================================

    @Test
    void getEmployeeById_shouldReturn404_whenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.getEmployeeById(99))
                .thenThrow(
                        new EmployeeNotFoundException(
                                "Employee not found with id: 99"
                        )
                );

        mockMvc.perform(
                        get("/employees/99")
                )
                .andExpect(status().isNotFound());
    }


    // =========================================================
    // TEST 4: CREATE EMPLOYEE
    // =========================================================

    @Test
    void addEmployee_shouldReturn201() throws Exception {

        EmployeeDTO request = new EmployeeDTO();

        request.setName("Rahul");
        request.setSalary(70000);


        EmployeeDTO response = new EmployeeDTO();

        response.setId(2);
        response.setName("Rahul");
        response.setSalary(70000);


        when(employeeService.convertToEntity(any(EmployeeDTO.class)))
                .thenReturn(new Employee());

        when(employeeService.saveEmployee(any()))
                .thenReturn(new Employee());

        when(employeeService.convertToDTO(any()))
                .thenReturn(response);


        mockMvc.perform(
                        post("/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.salary").value(70000));
    }


    // =========================================================
    // TEST 5: DELETE EMPLOYEE
    // =========================================================

    @Test
    void deleteEmployee_shouldReturn204() throws Exception {

        doNothing()
                .when(employeeService)
                .deleteEmployee(1);

        mockMvc.perform(
                        delete("/employees/1")
                )
                .andExpect(status().isNoContent());

        verify(employeeService)
                .deleteEmployee(1);
    }


    // =========================================================
    // TEST 6: UPDATE EMPLOYEE
    // =========================================================

    @Test
    void updateEmployee_shouldReturnUpdatedEmployee()
            throws Exception {

        EmployeeDTO request = new EmployeeDTO();

        request.setName("Janu Updated");
        request.setSalary(75000);


        EmployeeDTO response = new EmployeeDTO();

        response.setId(1);
        response.setName("Janu Updated");
        response.setSalary(75000);


        when(employeeService.updateEmployee(
                eq(1),
                any(EmployeeDTO.class)
        )).thenReturn(response);


        mockMvc.perform(
                        put("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Janu Updated"))
                .andExpect(jsonPath("$.salary").value(75000));


        verify(employeeService)
                .updateEmployee(
                        eq(1),
                        any(EmployeeDTO.class)
                );
    }


    // =========================================================
    // TEST 7: UPDATE SALARY - SUCCESS
    // =========================================================

    @Test
    void updateSalary_shouldReturn200_whenSalaryUpdated()
            throws Exception {

        when(employeeService.updateSalary(1, 80000))
                .thenReturn(1);

        mockMvc.perform(
                        put("/employees/salary/1")
                                .param("salary", "80000")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string("Salary updated successfully")
                );

        verify(employeeService)
                .updateSalary(1, 80000);
    }


    // =========================================================
    // TEST 8: UPDATE SALARY - EMPLOYEE NOT FOUND
    // =========================================================

    @Test
    void updateSalary_shouldReturn404_whenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.updateSalary(99, 80000))
                .thenReturn(0);

        mockMvc.perform(
                        put("/employees/salary/99")
                                .param("salary", "80000")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        content().string("Employee not found")
                );

        verify(employeeService)
                .updateSalary(99, 80000);
    }


    // =========================================================
    // TEST 9: UPDATE EMPLOYEE - NOT FOUND
    // =========================================================

    @Test
    void updateEmployee_shouldReturn404_whenEmployeeDoesNotExist()
            throws Exception {

        EmployeeDTO request = new EmployeeDTO();

        request.setName("Unknown Employee");
        request.setSalary(50000);


        when(employeeService.updateEmployee(
                eq(99),
                any(EmployeeDTO.class)
        )).thenThrow(
                new EmployeeNotFoundException(
                        "Employee not found with id: 99"
                )
        );


        mockMvc.perform(
                        put("/employees/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNotFound());


        verify(employeeService)
                .updateEmployee(
                        eq(99),
                        any(EmployeeDTO.class)
                );
    }


    // =========================================================
    // TEST 10: DELETE EMPLOYEE - NOT FOUND
    // =========================================================

    @Test
    void deleteEmployee_shouldReturn404_whenEmployeeDoesNotExist()
            throws Exception {

        doThrow(
                new EmployeeNotFoundException(
                        "Employee not found with id: 99"
                )
        )
                .when(employeeService)
                .deleteEmployee(99);


        mockMvc.perform(
                        delete("/employees/99")
                )
                .andExpect(status().isNotFound());


        verify(employeeService)
                .deleteEmployee(99);
    }


    // =========================================================
    // TEST 11: INVALID JSON REQUEST
    // =========================================================

    @Test
    void addEmployee_shouldReturn400_whenRequestIsInvalidJson()
            throws Exception {

        String invalidJson =
                "{ \"name\": \"Janu\", \"salary\": }";

        mockMvc.perform(
                        post("/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest());
    }
}