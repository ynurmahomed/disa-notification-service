package disa.notification.service.service;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import disa.notification.service.utils.SeafileUtil;

@Service
public class SeafileService1 {
	
	@Value("${disa.notifier.seafile.username}")
 	private String userName;
	
	@Value("${disa.notifier.seafile.password}") 
	private String password;

	public final String HEADER = "--header 'accept: application/json' ";
	
	RestTemplate restTemplate = new RestTemplate();
	
	public String getAuthenticationToken() throws Exception {
		String url = "https://share.csaude.org.mz/api2/auth-token/";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", userName);
        requestBody.put("password", password);
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        
        String jsonResponse = SeafileUtil.extractJson(response.getBody());
        String token = SeafileUtil.parseJson(jsonResponse).get("token").toString();
        
        return token.replace("\"", "");
		
	}
	
	public String getAnUploadLink(String repoId) throws Exception {
		String url = UriComponentsBuilder.fromHttpUrl("https://share.csaude.org.mz/api2/repos/" + repoId + "/upload-link/")
                .queryParam("p", "/")
                .toUriString();
		
		HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("authorization", "Token " + getAuthenticationToken());
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
        return SeafileUtil.getUploadLink(response.getBody());
	}
	
	public String uploadFile(String repoId, String attachmentName) throws Exception {
		String uploadUrl = getAnUploadLink(repoId) + "?ret-json=1";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("accept", "application/json");
        headers.set("authorization", "Token " + getAuthenticationToken());
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("parent_dir", "/");
        body.add("file", new FileSystemResource(Paths.get("temp").resolve(attachmentName).toString()));
        
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, entity, String.class);
        
        return response.getBody();
	}
}
