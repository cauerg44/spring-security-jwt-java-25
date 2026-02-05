package com.security.test.api.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.test.api.dto.request.LoginRequestDTO;
import com.security.test.api.dto.response.LoginResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TokenUtil {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String obtainAccessToken(MockMvc mockMvc, String username, String password) throws Exception {
		LoginRequestDTO loginRequest = new LoginRequestDTO(username, password);
		String jsonBody = objectMapper.writeValueAsString(loginRequest);

		ResultActions result = mockMvc
				.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		String resultString = result.andReturn().getResponse().getContentAsString();
		LoginResponseDTO loginResponse = objectMapper.readValue(resultString, LoginResponseDTO.class);
		
		return loginResponse.token();
	}
}

