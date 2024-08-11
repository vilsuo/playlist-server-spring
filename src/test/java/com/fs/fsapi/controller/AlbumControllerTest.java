package com.fs.fsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fs.fsapi.service.AlbumService;

@WebMvcTest(AlbumController.class)
public class AlbumControllerTest {
  
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AlbumService service;
}
