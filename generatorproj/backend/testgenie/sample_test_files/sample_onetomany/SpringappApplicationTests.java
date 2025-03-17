package com.examly.springapp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.util.Arrays;
import org.springframework.data.jpa.repository.Query;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.OneToMany;

import org.junit.jupiter.api.MethodOrderer;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class SpringappApplicationTests {

    @Autowired
    private MockMvc mockMvc;


    // Test adding a new event
    @Test
    @Order(2)
    public void testAddEvent() throws Exception {
        String eventJson = "{ \"eventName\": \"Tech Conference\", \"eventDate\": \"2025-03-15\", \"location\": \"New York\", \"description\": \"Annual Tech Conference\" }";
        
        mockMvc.perform(post("/api/event")
            .contentType(MediaType.APPLICATION_JSON)
            .content(eventJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.eventName").value("Tech Conference"))
            .andExpect(jsonPath("$.location").value("New York"));
    }



    @Test
    @Order(3)
    public void testDuplicateAddEvent() throws Exception {
        String eventJson = "{ \"eventName\": \"Tech Conference\", \"eventDate\": \"2025-03-15\", \"location\": \"New York\", \"description\": \"Annual Tech Conference\" }";
        
        mockMvc.perform(post("/api/event")
            .contentType(MediaType.APPLICATION_JSON)
            .content(eventJson))
            .andExpect(status().isConflict())
            .andExpect(content().string("An event with the same name, date, and location already exists."));

    }


    // Test retrieving an event by ID
    @Test
    @Order(4)
    public void testGetEventById() throws Exception {
        // Assuming an event with ID 1 exists from testAddEvent
        mockMvc.perform(get("/api/event/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.eventName").value("Tech Conference"));
    }

    // Test retrieving a non-existent event
    @Test
    @Order(5)
    public void testGetEventById_NotFound() throws Exception {
        mockMvc.perform(get("/api/event/999")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Event with ID 999 not found."));
    }

    // Test retrieving all events
    @Test
    @Order(6)
    public void testGetAllEvents() throws Exception {
        mockMvc.perform(get("/api/event")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].eventName").exists());

    }

    @Test
@Order(1)
public void testGetAllEvents_NoContent() throws Exception {
    // This test assumes that the event list is empty.
    mockMvc.perform(get("/api/event")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
}


    // Test adding a new ticket for an event
    @Test
    @Order(7)
    public void testAddTicket() throws Exception {
        String ticketJson = "{  \"price\": 50.0, \"seatNumber\": \"A1\", \"ticketType\": \"General\" }";
        
        // Assuming an event with ID 1 exists
        mockMvc.perform(post("/api/ticket/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ticketJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.seatNumber").value("A1"));
    }

    // Test retrieving tickets by event date and location
    @Test
    @Order(8)
    public void testGetTicketsByEventDateAndLocation() throws Exception {
        // This assumes that your service returns tickets for eventDate "2025-03-15" and location "New York"
        mockMvc.perform(get("/api/ticket/search/2025-03-15/New York")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].seatNumber").exists());

    }

    // Test when no tickets match the provided event date and location
    @Test
    @Order(9)
    public void testGetTicketsByEventDateAndLocation_NoTickets() throws Exception {
        mockMvc.perform(get("/api/ticket/search/2025-04-01/Los Angeles")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(content().string("No tickets found for this event date and location."));
    }

    // Test deleting an existing ticket
    @Test
    @Order(10)
    public void testDeleteTicket() throws Exception {
        // Assuming a ticket with ID 1 exists from testAddTicket
        mockMvc.perform(delete("/api/ticket/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    // Test deleting a non-existent ticket
    @Test
    @Order(11)
    public void testDeleteTicket_NotFound() throws Exception {
        mockMvc.perform(delete("/api/ticket/999")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Ticket not found with ID: 999"));
    }



     @Test
        void testFoldersAndFilesExist() {
            // Test for folders
            String[] directories = {
                "src/main/java/com/examly/springapp/controller",
                "src/main/java/com/examly/springapp/model",
                "src/main/java/com/examly/springapp/repository",
                "src/main/java/com/examly/springapp/service"
            };
    
            for (String directoryPath : directories) {
                File directory = new File(directoryPath);
                assertTrue(directory.exists() && directory.isDirectory(), "Directory does not exist: " + directoryPath);
            }
    
            // Test for files in the controller folder
            String[] controllerFiles = {
                "src/main/java/com/examly/springapp/controller/EventController.java",
                "src/main/java/com/examly/springapp/controller/TicketController.java"
            };
    
            for (String filePath : controllerFiles) {
                File file = new File(filePath);
                assertTrue(file.exists() && file.isFile(), "File does not exist: " + filePath);
            }
    
            // Test for files in the model folder
            String[] modelFiles = {
                "src/main/java/com/examly/springapp/model/Event.java",
                "src/main/java/com/examly/springapp/model/Ticket.java"
            };
    
            for (String filePath : modelFiles) {
                File file = new File(filePath);
                assertTrue(file.exists() && file.isFile(), "File does not exist: " + filePath);
            }
    
            // Test for files in the repository folder
            String[] repoFiles = {
                "src/main/java/com/examly/springapp/repository/EventRepo.java",
                "src/main/java/com/examly/springapp/repository/TicketRepo.java"
            };
    
            for (String filePath : repoFiles) {
                File file = new File(filePath);
                assertTrue(file.exists() && file.isFile(), "File does not exist: " + filePath);
            }
    
            // Test for files in the service folder
            String[] serviceFiles = {
                "src/main/java/com/examly/springapp/service/EventService.java",
                "src/main/java/com/examly/springapp/service/TicketService.java",
                "src/main/java/com/examly/springapp/service/EventServiceImpl.java",
                "src/main/java/com/examly/springapp/service/TicketServiceImpl.java"
            };
    
            for (String filePath : serviceFiles) {
                File file = new File(filePath);
                assertTrue(file.exists() && file.isFile(), "File does not exist: " + filePath);
            }
        }



     @Test
    void testEventServiceInterfaceExists() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.service.EventService");
            assertNotNull(clazz, "EventService interface should exist.");
            assertTrue(clazz.isInterface(), "EventService should be an interface.");
        } catch (ClassNotFoundException e) {
            fail("EventService interface not found.");
        }
    }

    @Test
    void testTicketServiceInterfaceExists() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.service.TicketService");
            assertNotNull(clazz, "TicketService interface should exist.");
            assertTrue(clazz.isInterface(), "TicketService should be an interface.");
        } catch (ClassNotFoundException e) {
            fail("TicketService interface not found.");
        }
    }


    @Test
    public void testQueryAnnotationPresentInTicketRepo() {
        try {
            Class<?> ticketRepoClass = Class.forName("com.examly.springapp.repository.TicketRepo");

            Method[] methods = ticketRepoClass.getMethods();

            boolean hasQueryAnnotation = Arrays.stream(methods)
                    .anyMatch(method -> Arrays.stream(method.getDeclaredAnnotations())
                            .anyMatch(annotation -> annotation.annotationType().equals(Query.class)));

            assertTrue(hasQueryAnnotation,
                    "@Query annotation should be present on at least one method in TicketRepo");
        } catch (ClassNotFoundException e) {
            fail("TicketRepo class not found. Ensure the class name and package are correct.");
        }
    }



    @Test
    public void testOneToManyAnnotationPresentInEvent() {
        try {
            Class<?> eventClass = Class.forName("com.examly.springapp.model.Event");
            Field ticketField = eventClass.getDeclaredField("tickets");
            OneToMany oneToManyAnnotation = ticketField.getAnnotation(OneToMany.class);

            assertNotNull(oneToManyAnnotation,
                    "@OneToMany annotation should be present on 'tickets' field in Event class");
        } catch (ClassNotFoundException e) {
            fail("Event class not found");
        } catch (NoSuchFieldException e) {
            fail("Field 'tickets' not found in Event class");
        }
    }



}
