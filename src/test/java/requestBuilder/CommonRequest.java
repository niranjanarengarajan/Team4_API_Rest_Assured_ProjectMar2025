package requestBuilder;

	

import static io.restassured.RestAssured.given;

import java.util.Map;

import org.testng.asserts.SoftAssert;

import common.ConfigReader;
import common.Utils;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CommonRequest {
    private RequestSpecification requestspecification;
    private Response response;
  

    
    public RequestSpecification basewithValidauth() {
       String token = Utils.get("authToken", String.class); 
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Token is null or empty");
        }
        return given()
                .baseUri(ConfigReader.getProperty("Base_Url"))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .contentType("application/json");
        	
    }

    // Call this function for requests without authentication
    public RequestSpecification basewithNoauth() {
    return given()
    		.baseUri(ConfigReader.getProperty("Base_Url"))
    		//.header("Authorization", "Bearer ")
    		.header("Accept", "application/json")
    		.contentType("application/json");
}
 
    public RequestSpecification basewithInvalidUrl() {
        String token = Utils.get("authToken", String.class); 
         if (token == null || token.isEmpty()) {
             throw new IllegalStateException("Token is null or empty");
         }
         return given()
                 .baseUri(ConfigReader.getProperty("Invalid_Base_Url"))
                 .header("Authorization", "Bearer " + token)
                 .header("Accept", "application/json")
                 .contentType("application/json");
         	
     }

		
    public void validateStatusCode(Response response, Map<String, String> testData) {
/*******************      Soft Assert
    	Soft Assert collects errors during @Test is running. It does not throw an exception when an assert fails. 
    	The execution will continue with the next step after the assert statement. 
    	If you need to throw an exception (if such occurs), you need to use assertAll() method as a last statement in the @Test. 
    	The test suite again continues with next @Test as it is.

    	Unlike Hard Results, for Soft Asserts, we need to create an object to use them. This is how it looks:*********************/


        SoftAssert softAssert = new SoftAssert();  // Create SoftAssert instance
        
        int expectedStatusCode = (int) Double.parseDouble(testData.get("expectedStatuscode"));
        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode, "Status code mismatch!");
        softAssert.assertAll();  
    }

    public void validateStatusLine(Response response, Map<String, String> testData) {
        SoftAssert softAssert = new SoftAssert(); 

        String expectedStatusLine = testData.get("expectedStatusLine");
        softAssert.assertEquals(response.getStatusLine(), expectedStatusLine, "Status line mismatch!");
        softAssert.assertAll(); 
    }

    public void validateContentType(Response response, Map<String, String> testData) {
        SoftAssert softAssert = new SoftAssert();
        String expectedContentType = "application/json";
        softAssert.assertEquals(response.getContentType(), expectedContentType, "Content-Type mismatch!");
        softAssert.assertAll();  
    }

    public void validateSchema(Response response, String schemaPath) {
        SoftAssert softAssert = new SoftAssert(); 

        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
        } catch (AssertionError e) {
            softAssert.fail("Schema validation failed: " + e.getMessage());
        }

        softAssert.assertAll(); 
    }

    public void validateResponseTime(Response response) {
        SoftAssert softAssert = new SoftAssert(); 
        softAssert.assertTrue(response.time() <= 4000, "Response time is too long. Actual time: " + response.time() + " ms.");
        softAssert.assertAll(); 
    }

    public void validateErrorMessage(Response response, Map<String, String> testData) {
        SoftAssert softAssert = new SoftAssert(); 

        String expectedMessage = testData.get("expectedErrorMessage");
        String actualMessage = response.jsonPath().getString("message");
        softAssert.assertEquals(actualMessage, expectedMessage, "Error message mismatch!");

        softAssert.assertAll();  
    }

   

}
