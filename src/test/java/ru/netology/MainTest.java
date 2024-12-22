package ru.netology;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    String[] employee1;
    String[] employee2;
    Employee employeeTest1;
    Employee employeeTest2;
    String employeeJson;
    String fileName;
    String[] columnMapping;

    @BeforeEach
    public void beforeEach() {
        employee1 = "1,John,Smith,USA,25".split(",");
        employee2 = "2,Inav,Petrov,RU,23".split(",");
        employeeTest1 = new Employee(1, "John", "Smith", "USA", 25);
        employeeTest2 = new Employee(2, "Inav", "Petrov", "RU", 23);
        employeeJson = "[\n {\n \"id\": 1,\n\"firstName\": \"John\",\n \"lastName\":\"Smith\",\n\"country\": \"USA\",\n\"age\": 25\n}\n]";
        fileName = "data.csv";
        columnMapping = new String[]{"id", "firstName", "lastName", "country", "age"};
    }

    @AfterEach
    public void afterEach() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("data.json");
        writer.print("");
        writer.close();
    }

    @org.junit.jupiter.api.Test
    void checkingParseCSVFoReturnAnEmptyList() {
        assertTrue(Main.parseCSV(columnMapping, "data_empty.csv").isEmpty());
    }

    @org.junit.jupiter.api.Test
    void checkingParseCSVFoReturnException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                Main.parseCSV(columnMapping, "data_exception"));
        assertEquals("Не удается найти указанный файл", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void checkingParseCSVFoReturnList() {
        List<Employee> employees = Main.parseCSV(columnMapping, fileName);
        assertEquals(2, employees.size());
        assertEquals(employees.get(0), employeeTest1);
        assertEquals(employees.get(1), employeeTest2);
    }

    @org.junit.jupiter.api.Test
    void checkingForNullListToJson() {
        List<Employee> employees = new ArrayList<>();
        employees.add(employeeTest1);
        assertNull(Main.listToJson(new ArrayList<>()));
        assertEquals(Main.listToJson(employees).replaceAll(" ", ""),
                employeeJson.replaceAll(" ", ""));
    }

    @org.junit.jupiter.api.Test
    void checkingWriteString() {
        List<Employee> employees = new ArrayList<>();
        employees.add(employeeTest1);
        String json = Main.listToJson(employees);
        assertTrue(new File("data.json").length() == 0);
        Main.writeString(json, "data.json");
        assertTrue(new File("data.json").length() != 0);
    }

    @org.junit.jupiter.api.Test
    void checkingParseXML() throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = Main.parseXML("data.xml");
        assertEquals(employees.get(0), employeeTest1);
        assertEquals(employees.get(1), employeeTest2);
    }

    @org.junit.jupiter.api.Test
    void test_readString() {
        Main.writeString(employeeJson, "data.json");
        assertEquals(Main.readString("data.json").replaceAll(" ", "")
                        .replaceAll("\n", ""),
                employeeJson.replaceAll(" ", "").replaceAll("\n", ""));
    }

    @org.junit.jupiter.api.Test
    void test_jsonToList() {
        List<Employee> employees = new ArrayList<>();
        employees.add(employeeTest1);
        String json = Main.listToJson(employees);
        Main.writeString(json, "data.json");
        assertEquals(employees, Main.jsonToList(Main.listToJson(employees)));
    }
}
