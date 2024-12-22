package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv", true))) {
            writer.writeNext(employee1);
            writer.writeNext(employee2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        List<Employee> employeeList = parseXML("data.xml");
        writeString(listToJson(employeeList), "data2.json");

        String jsonList = readString("new_data.json");
        List<Employee> jsonEmployees = jsonToList(jsonList);
        jsonEmployees.forEach(System.out::println);

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            throw new RuntimeException("Не удается найти указанный файл");
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        if (list.isEmpty()) {
            return null;
        }
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setPrettyPrinting().create();
            Type listType = new TypeToken<List<Employee>>() {
            }.getType();

        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String path) {
     if (json!=null) {
         try (FileWriter writer = new FileWriter(path)) {
             writer.write(json);
             writer.flush();
         } catch (IOException e) {
             throw new RuntimeException(e.getMessage());
         }
     }
    }

    public static List<Employee> parseXML(String path) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(path));
        Node node = doc.getDocumentElement();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                employees.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return employees;
    }

    public static String readString(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s + "\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    public static List<Employee> jsonToList(String jsonList) {
        List<Employee> employees = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(jsonList);
            JSONArray jsonObject = (JSONArray) obj;
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object o : jsonObject) {
                Employee employee = gson.fromJson(o.toString(), Employee.class);
                employees.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }
}