package com.fs.fsapi.metallum.driver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fs.fsapi.metallum.base.MetallumController;

@RestController
@RequestMapping("/metallum/driver")
public class MetallumDriverController extends MetallumController {

  private final MetallumDriverService service;

  public MetallumDriverController(MetallumDriverService service) {
    super(service);
    this.service = service;
  }
  
  @PostMapping("/cookie")
  public ResponseEntity<Void> setCookie(@RequestBody String value) {
    service.setBypassCookie(value);
    
    return ResponseEntity
      .noContent()
      .build();
  }
}
