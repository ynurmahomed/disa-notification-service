package disa.notification.service.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import disa.notification.service.utils.SeafileUtil;

@Service
public class SeafileService {
	
	@Value("${disa.notifier.seafile.username}")
 	private String userName;
	
	@Value("${disa.notifier.seafile.password}") 
	private String password;
	
	public final String HEADER = "--header 'accept: application/json' ";
	
	public String getAuthenticationToken() throws Exception {
		String authTokenCommand = "curl --request POST --url https://share.csaude.org.mz/api2/auth-token/ "
				+ ""+HEADER+"" 
				+ "--header 'content-type: application/json' "
				+ "--data '{\"username\": \""+userName+"\", \"password\": \""+password+"\"}'";
		
		String authTokenResponse = executeCommand(authTokenCommand);
		String jsonResponse = SeafileUtil.extractJson(authTokenResponse); 
		String token = SeafileUtil.parseJson(jsonResponse).get("token").toString();
		return token.replaceAll("\"", "");
	}
	
	public String getAnUploadLink(String repoId) throws Exception {
		String uploadLinkCommand = "curl --request GET "
				+ "--url 'https://share.csaude.org.mz/api2/repos/"+repoId+"/upload-link/?p=%2F' "
				+ ""+HEADER+""
				+ "--header 'authorization: Token " + getAuthenticationToken() + "'";
		String uploadLinkResponse = executeCommand(uploadLinkCommand);
		return SeafileUtil.getUploadLink(uploadLinkResponse);
	}
	
	public String uploadFile(String repoId, String attachmentName) throws Exception {
		String uploadCommand = "curl --request POST "
			     + "--url '"+getAnUploadLink(repoId)+"?ret-json=1' "
			     + ""+HEADER+""
			     + "--header 'authorization: Token " + getAuthenticationToken() + "' "
			     + "--header 'content-type: multipart/form-data' "
			     + "--form parent_dir=/ "
			     + "--form file=@" + Paths.get("temp").resolve(attachmentName).toString();
		return executeCommand(uploadCommand);
	}
	
	public String executeCommand(String command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder()
                .command("bash", "-c", command)
                .redirectErrorStream(true)
                .start();

        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new RuntimeException("Command exited with non-zero exit code: " + exitCode);
        }

        return output.toString();
    }
}
