package jakwag.tasktracker;

import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "spring.flyway.clean-disabled=false")
@Testcontainers
@ActiveProfiles("intTest")
public class TaskCrudTest {

    private static final String TASKS_PATH_WITH_SLASH = "/api/tasks/";
    private static final String TASKS_PATH = "/api/tasks";
    private final String TASK_JSON_EMPTY_STATUS = "{\n" +
            "  \"title\": \"Test title 123\",\n" +
            "  \"description\" : \"" + "Test description 123" + "\",\n" +
            "  \"status\" : \"\"\n" +
            "}";
    private final String TASK_JSON_NO_STATUS = "{\n" +
            "  \"title\": \"Test title 123\",\n" +
            "  \"description\" : \"" + "Test description 123" + "\"\n" +
            "}";
    private final String TASK_JSON = "{\n" +
            "  \"title\": \"Test title 123\",\n" +
            "  \"description\" : \"" + "Test description 123" + "\",\n" +
            "  \"status\" : \"Open\"\n" +
            "}";


    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mvc;

    @BeforeEach
    public void setup() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
//                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void cleanup(@Autowired Flyway flyway) throws Exception {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldReturnEmptyList_whenGetTasksOnEmptyDb() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void shouldReturnOneElement_whenOneTaskIsAdded() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)));
        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("[0].title").value("Test title 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].status").value("Open"));
    }

    @Test
    void shouldReturnTwoElements_whenTwoTasksAreAdded() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)));

        mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content("{\n" +
                        "  \"title\": \"Test title 321\",\n" +
                        "  \"description\" : \"" + "Test description 321" + "\",\n" +
                        "  \"status\" : \"Done\"\n" +
                        "}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)));

        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("length()").value(2));
    }

    @Test
    void shouldReturnAddedElement_whenGettingById() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)))
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        assertNotNull(location);

        String id = location.substring(TASKS_PATH_WITH_SLASH.length());

        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH_WITH_SLASH + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("Test title 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("Open"));
    }

    @Test
    void shouldReturnEmptyArray_whenTheOnlyElementIsRemoved() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)))
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        assertNotNull(location);

        String id = location.substring(TASKS_PATH_WITH_SLASH.length());

        mvc.perform(MockMvcRequestBuilders.delete(TASKS_PATH_WITH_SLASH + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("length()").value(0));
    }

    @Test
    void shouldReturn404_whenTheOnlyElementIsRemoved() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)))
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        assertNotNull(location);

        String id = location.substring(TASKS_PATH_WITH_SLASH.length());

        mvc.perform(MockMvcRequestBuilders.delete(TASKS_PATH_WITH_SLASH + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH_WITH_SLASH + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void shouldReturnUpdatedElement_whenElementGetsUpdatedWithPut() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)))
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        assertNotNull(location);

        String id = location.substring(TASKS_PATH_WITH_SLASH.length());

        mvc.perform(MockMvcRequestBuilders.put(TASKS_PATH).content("{\n" +
                "  \"id\": " + id + ",\n" +
                "  \"title\": \"Some other test title 123\",\n" +
                "  \"description\" : \"" + "No desc" + "\",\n" +
                "  \"status\" : \"Done\"\n" +
                "}").contentType(MediaType.APPLICATION_JSON));

        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH_WITH_SLASH + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("Some other test title 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("No desc"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("Done"));
    }

    @Test
    void shouldReturnTaskWithUpdatedOnlyTheNonNullFields_whenUsingPatchMethod() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)))
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        assertNotNull(location);

        String id = location.substring(TASKS_PATH_WITH_SLASH.length());

        mvc.perform(MockMvcRequestBuilders.patch(TASKS_PATH_WITH_SLASH + id).content("{\n" +
                "  \"status\" : \"Done\"\n" +
                "}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("Test title 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("Done"));

        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH_WITH_SLASH + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("Test title 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("Done"));

        mvc.perform(MockMvcRequestBuilders.patch(TASKS_PATH_WITH_SLASH + id).content("{\n" +
                        "  \"title\" : \"Changed title 321\"\n" +
                        "}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("Changed title 321"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("Done"));

        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH_WITH_SLASH + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("Changed title 321"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("Done"));
    }

    @Test
    void shouldReturnNotFound_whenTryingToPatchUnassignedId() throws Exception {
        int idWithNoAssignedEntity = 1; //DB should be empty, ID could have any value
        mvc.perform(MockMvcRequestBuilders.patch(TASKS_PATH_WITH_SLASH + idWithNoAssignedEntity).content("{\n" +
                        "  \"title\" : \"Changed title 321\"\n" +
                        "}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void shouldReturnElementWithStatusOpen_whenCreatingElementWithoutStatus() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON_NO_STATUS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)));
        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("[0].title").value("Test title 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].status").value("Open"));
    }

    @Test
    void shouldReturnElementWithStatusOpen_whenCreatingElementWithStatusEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TASKS_PATH).content(TASK_JSON_EMPTY_STATUS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(TASKS_PATH_WITH_SLASH)));
        mvc.perform(MockMvcRequestBuilders.get(TASKS_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("[0].title").value("Test title 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].description").value("Test description 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].status").value("Open"));
    }
}
