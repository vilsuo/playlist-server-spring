package com.fs.fsapi.album;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@WebMvcTest(AlbumController.class)
public class AlbumControllerTest {

  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AlbumService service;

  @Test
	public void greetingShouldReturnMessageFromService() throws Exception {
    when(service.findAll()).thenReturn(List.of());

    //String expected = objectMapper.writeValueAsString(albums);

		MvcResult result = this.mockMvc
      .perform(get("/albums"))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    CollectionType collectionType = objectMapper
      .getTypeFactory()
      .constructCollectionType(List.class, Album.class);

    List<Album> albums = objectMapper.readValue(json, collectionType);

    assertEquals(0, albums.size());;
	}
}
