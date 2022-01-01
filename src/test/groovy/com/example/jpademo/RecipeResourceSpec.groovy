package com.example.jpademo

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class RecipeResourceSpec extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private RecipeRepository recipeRepository
    @Autowired
    private CategoryRepository categoryRepository;

    def "should create recipe"() {
        given:
        def mapper = new ObjectMapper()
        def request = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString([
                title      : "Title",
                description: "Description",
                ingredients: [
                    [
                        title      : "a",
                        description: "a"
                    ],
                    [
                        title      : "b",
                        description: "b"
                    ]
                ],
                categories : [
                    [
                        title: "cat3"
                    ]
                ]
            ])

        when:
        def response = mvc.perform(post("/recipes")
            .contentType(APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()

        then:
        assertEquals(response, new JSONObject(["id": 1]), true)
        recipeRepository.findByTitle("Title").isPresent()
        with(recipeRepository.findByTitle("Title").orElseThrow(), {
            it.title == "Title"
            it.description == "Description"
        })

    }

    def "should create recipe and not duplicate categories"() {
        given:
        def mapper = new ObjectMapper()
        def request = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            [
                title      : "Title",
                description: "Description",
                ingredients: [
                    [
                        title      : "a",
                        description: "a"
                    ],
                    [
                        title      : "b",
                        description: "b"
                    ]
                ],
                categories : [
                    [
                        title: "cat1"
                    ]
                ]
            ]
        )

        when:
        2.times {
            mvc.perform(post("/recipes")
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
        }

        then:
        recipeRepository.findAllByTitle("Title").size() >= 2
        categoryRepository.findAllByTitle("cat1").size() == 1


    }

    def "should get recipe"() {
        given:
        def mapper = new ObjectMapper()
        def request = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            [
                title      : "Title",
                description: "Description",
                ingredients: [
                    [
                        title      : "a",
                        description: "a"
                    ],
                    [
                        title      : "b",
                        description: "b"
                    ]
                ],
                categories : [
                    [
                        title: "cat1"
                    ],
                    [
                        title: "cat2"
                    ]
                ]
            ]
        )
        and: "create recipe"
        def createdRecipe = mvc.perform(post("/recipes")
            .contentType(APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()
        def createdRecipeId = mapper.readTree(createdRecipe).get("id").asLong()

        when:
        def response = mvc.perform(get("/recipes/${createdRecipeId}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()


        then:
        assertEquals(response, new JSONObject(
            [
                title      : "Title",
                description: "Description",
                ingredients: [
                    [
                        title      : "a",
                        description: "a"
                    ],
                    [
                        title      : "b",
                        description: "b"
                    ]
                ],
                categories : [
                    [
                        title: "cat1"
                    ],
                    [
                        title: "cat2"
                    ]
                ]
            ]), true)

    }
}
