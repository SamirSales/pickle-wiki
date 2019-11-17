package io.github.samirsales.controller;

import io.github.samirsales.model.dto.CredentialDTO;
import io.github.samirsales.model.dto.ImageDTO;
import io.github.samirsales.model.dto.RoleDTO;
import io.github.samirsales.model.dto.user.UserCreationDTO;
import io.github.samirsales.model.dto.user.UserDTO;
import io.github.samirsales.model.entity.UserEntity;
import io.github.samirsales.model.enums.Gender;
import io.github.samirsales.repository.UserRepository;
import io.github.samirsales.utils.UserEntityGenerator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerRestTest {

    @Autowired
    @SuppressWarnings("unused")
    private TestRestTemplate testRestTemplate;

    @Autowired
    @SuppressWarnings("unused")
    private static UserRepository userRepository;

    private final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final String USER_CONTROLLER_URL_PREFIX = "/users";

    @AfterClass
    public static void reset(){
        if(userRepository != null){
            userRepository.deleteAll();
        }
    }

    @Test
    public void verifyIfGetAllWorkingWithoutToken() {
        ResponseEntity<Object> responseEntity = testRestTemplate.getForEntity(USER_CONTROLLER_URL_PREFIX, Object.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void verifyIfGetAllIsOk() {
        HttpEntity<?> requestEntity = getHttpEntityWithAuthorization();
        ResponseEntity<List<UserDTO>> responseEntity = testRestTemplate.exchange(USER_CONTROLLER_URL_PREFIX,
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<UserDTO>>(){});

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertFalse(Objects.requireNonNull(responseEntity.getBody()).isEmpty());
    }

    @Test
    public void verifyIfGetUserByIdWorks() {
        HttpEntity<?> requestEntity = getHttpEntityWithAuthorization();
        Long id = 1L;
        String url = USER_CONTROLLER_URL_PREFIX + "/" + id;
        ResponseEntity<UserDTO> responseEntity = testRestTemplate
                .exchange(url, HttpMethod.GET, requestEntity, UserDTO.class);
        
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertNotNull(responseEntity.getBody());
    }

    @Test
    public void verifyIfGetUserByIdNotFound() {
        HttpEntity<?> requestEntity = getHttpEntityWithAuthorization();
        Long nonexistentId = 999L;
        String url = USER_CONTROLLER_URL_PREFIX + "/" + nonexistentId;

        ResponseEntity<UserDTO> responseEntity = testRestTemplate
                .exchange(url,  HttpMethod.GET, requestEntity, UserDTO.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void verifyIfInsertUserWorks() {
        UserCreationDTO genericUserCreationDto = getGenericUserCreationDto();

        String authenticationToken = getAuthenticationToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER_NAME, authenticationToken);

        HttpEntity<UserCreationDTO> requestEntity = new HttpEntity<>(genericUserCreationDto, headers);
        ResponseEntity<UserCreationDTO> responseEntity = testRestTemplate
                .exchange(USER_CONTROLLER_URL_PREFIX, HttpMethod.POST, requestEntity, UserCreationDTO.class);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    private UserCreationDTO getGenericUserCreationDto(){
        String name = "name";
        String username = "username";
        String password = "123456";
        String email = "user@email.com";
        final ImageDTO imageProfileDTO = null;
        final Set<RoleDTO> roles = new HashSet<>();
        return new UserCreationDTO(name,username,password,email, Gender.MALE, imageProfileDTO, roles);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void verifyIfInsertUserWithoutRequiredParameters() {
        UserDTO userDtoWithoutRequiredParameters = getUserDtoWithoutRequiredParametersToInsert();

        String authenticationToken = getAuthenticationToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER_NAME, authenticationToken);

        HttpEntity<UserDTO> requestEntity = new HttpEntity<>(userDtoWithoutRequiredParameters, headers);
        ResponseEntity<List<String>> responseEntity = testRestTemplate.exchange(USER_CONTROLLER_URL_PREFIX,
                HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<String>>(){});

        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getBody().contains("The 'name' attribute must be filled."));
        Assert.assertTrue(responseEntity.getBody().contains("The 'username' attribute must be filled."));
        Assert.assertTrue(responseEntity.getBody().contains("The 'password' attribute must be filled."));
    }

    private UserDTO getUserDtoWithoutRequiredParametersToInsert(){
        UserEntity userEntity = UserEntityGenerator.getUserEntityGeneratedById(4L);
        UserEntity userEntityWithoutRequiredParameters = userEntity
                .toBuilder().id(null).name("").username("").password("").build();
        return new UserDTO(userEntityWithoutRequiredParameters);
    }

    private HttpEntity<?> getHttpEntityWithAuthorization(){
        String authenticationToken = getAuthenticationToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER_NAME, authenticationToken);
        return new HttpEntity<>(headers);
    }

    private String getAuthenticationToken() {
        ResponseEntity<String> responseEntityAuthentication = getResponseEntityAuthentication();
        return responseEntityAuthentication.getHeaders().getFirst(AUTHORIZATION_HEADER_NAME);
    }

    private ResponseEntity<String> getResponseEntityAuthentication() {
        CredentialDTO credentialDTO = getAdminCredentialDTO();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CredentialDTO> requestEntity = new HttpEntity<>(credentialDTO, headers);
        return testRestTemplate.exchange("/login", HttpMethod.POST, requestEntity, String.class);
    }

    private CredentialDTO getAdminCredentialDTO(){
        String username = "admin";
        String password = "admin";
        return new CredentialDTO(username, password);
    }
}
