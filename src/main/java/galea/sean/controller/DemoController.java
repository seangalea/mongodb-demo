package galea.sean.controller;

import galea.sean.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {

  TokenService tokenService;

  @Autowired
  public void setTokenService(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @GetMapping(
      value = "/tokens",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getTokens(@RequestParam(required = false) String tag) {
    if (tag == null) {

      List allTokens = tokenService.getTokens();
      return ResponseEntity.ok(allTokens);
    }
    List allTokens = tokenService.getTokens(tag);
    return ResponseEntity.ok(allTokens);
  }
  @GetMapping(
          value = "/tokens/tags",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getTokenTags() {

      List<String> allTags = tokenService.getAllTokenTags();
      return ResponseEntity.ok(allTags);
  }

  @PostMapping(
      value = "/token",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> postTokens(@RequestBody Map request) {
    tokenService.addToken(request);
    return ResponseEntity.ok().build();
  }
}
