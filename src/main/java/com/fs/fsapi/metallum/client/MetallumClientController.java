package com.fs.fsapi.metallum.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fs.fsapi.metallum.base.MetallumController;

@RestController
@RequestMapping("/metallum/client")
public class MetallumClientController extends MetallumController {

  public MetallumClientController(MetallumClientService service) {
    super(service);
  }
}
