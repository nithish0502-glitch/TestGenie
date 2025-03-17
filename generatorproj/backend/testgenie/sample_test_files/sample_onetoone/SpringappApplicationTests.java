package com.examly.springapp;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@SpringBootTest(classes = SpringappApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpringappApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(2)
    void testCreatePatient_Success() throws Exception {
        String patientJson = "{"
                + "\"name\": \"John Doe\","
                + "\"age\": 30,"
                + "\"gender\": \"Male\","
                + "\"contactNumber\": \"1234567890\","
                + "\"address\": \"123 Main St, New York\""
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contactNumber").value("1234567890"))
                .andReturn();
    }

    @Test
    @Order(3)
    void testCreatePatient_MissingFields() throws Exception {
        String patientJson = "{ \"name\": \"\", \"contactNumber\": \"\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict()) 
                .andExpect(MockMvcResultMatchers.content().string("Patient name and contact number are required."));
    }

    @Test
    @Order(4)
    void testGetAllPatients_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/patient")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].address").value("123 Main St, New York"));
    }

    @Test
    @Order(1)
    void testGetAllPatients_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/patient")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No patients found."));
    }

    @Test
    @Order(5)
    void testUpdatePatient_Success() throws Exception {
        String patientJson = "{ \"name\": \"Updated Name\", \"contactNumber\": \"9876543210\" }";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/patient/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contactNumber").value("9876543210"));
    }

    @Test
    @Order(6)
    void testUpdatePatient_NotFound() throws Exception {
        String patientJson = "{ \"name\": \"Updated Name\", \"contactNumber\": \"9876543210\" }";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/patient/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Patient with ID 99 not found."));
    }

    @Test
    @Order(7)
    void testCreateMedicalRecord_Success() throws Exception {
        String medicalRecordJson = "{"
                + "\"recordNumber\": \"MR-12345\","
                + "\"diagnosis\": \"Flu\","
                + "\"treatment\": \"Rest and fluids\","
                + "\"doctorName\": \"Dr. Smith\","
                + "\"visitDate\": \"2024-03-12T00:00:00.000+00:00\""
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/medicalRecord/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated()) 
                .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("Flu"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.treatment").value("Rest and fluids"))
                .andReturn();
    }
 
    @Test
    @Order(8)
    public void testPatientHasLinkedMedicalRecord() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/patient"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].medicalRecord.diagnosis").value("Flu"));
    }

    @Test
    @Order(9)
    void testCreateMedicalRecord_PatientNotFound() throws Exception {
        String medicalRecordJson = "{ \"diagnosis\": \"Flu\", \"treatment\": \"Rest and fluids\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/medicalRecord/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Patient with ID 99 not found."));
    }

    @Test
    @Order(10)
    void testGetMedicalRecordById_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/medicalRecord/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) 
                .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("Flu"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.treatment").value("Rest and fluids"));
    }

    @Test
    @Order(11)
    void testGetMedicalRecordsByDoctor_Success() throws Exception {
        String doctorName = "Dr. Smith";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/medicalRecord/byDoctor/" + doctorName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) 
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].doctorName").value(doctorName));
    }

    @Test
    @Order(12)
    void testGetMedicalRecordById_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/medicalRecord/100")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Medical record with ID 100 not found."));
    }

    @Test
    @Order(13)
    void testDeleteMedicalRecord_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/medicalRecord/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Medical record deleted successfully."));
    }

    @Test
    @Order(14)
    void testDeleteMedicalRecord_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/medicalRecord/200")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Medical record with ID 200 not found."));
    }

    @Test
    @Order(15)
    public void testQueryAnnotationPresentInMedicalRecordRepository() {
        try {
            Class<?> repoClass = Class.forName("com.examly.springapp.repository.MedicalRecordRepository");

            Method[] methods = repoClass.getDeclaredMethods();

            boolean hasQueryAnnotation = Arrays.stream(methods)
                    .flatMap(method -> Arrays.stream(method.getAnnotations()))
                    .anyMatch(annotation -> annotation.annotationType().equals(Query.class));

            assertTrue(hasQueryAnnotation,
                    "@Query annotation should be present on at least one method in MedicalRecordRepository");
        } catch (ClassNotFoundException e) {
            fail("MedicalRecordRepository class not found");
        }
    }

    @Test
    public void testFoldersExist() {
        String[] folders = {
                "src/main/java/com/examly/springapp/controller",
                "src/main/java/com/examly/springapp/model",
                "src/main/java/com/examly/springapp/repository",
                "src/main/java/com/examly/springapp/service",
                "src/main/java/com/examly/springapp/exception"
        };

        for (String folderPath : folders) {
            File directory = new File(folderPath);
            assertTrue(directory.exists() && directory.isDirectory(),
                    "Folder does not exist: " + folderPath);
        }
    }

    @Test
    public void testFilesExist() {
        String[] files = {
                "src/main/java/com/examly/springapp/controller/PatientController.java",
                "src/main/java/com/examly/springapp/controller/MedicalRecordController.java",
                "src/main/java/com/examly/springapp/model/Patient.java",
                "src/main/java/com/examly/springapp/model/MedicalRecord.java",
                "src/main/java/com/examly/springapp/repository/PatientRepository.java",
                "src/main/java/com/examly/springapp/repository/MedicalRecordRepository.java",
                "src/main/java/com/examly/springapp/service/PatientService.java",
                "src/main/java/com/examly/springapp/service/MedicalRecordService.java",
                "src/main/java/com/examly/springapp/service/PatientServiceImpl.java",
                "src/main/java/com/examly/springapp/service/MedicalRecordServiceImpl.java"
        };

        for (String filePath : files) {
            File file = new File(filePath);
            assertTrue(file.exists() && file.isFile(),
                    "File does not exist: " + filePath);
        }
    }

}