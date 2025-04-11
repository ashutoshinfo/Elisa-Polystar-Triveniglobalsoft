# Elisa-Polystar-Triveniglobalsoft

## How to Run the Project:
**Start Server 1**
- Run Server 1 on port 8081:

**Start Server 2**
- Run Server 2 on port 8082:

**Start the Client Application**
- Run the Client application, which will communicate with both servers.

**Make an API Call**
- Make a GET request to the following endpoint:  
  `` http://localhost:8080/count-words  ``

**Expected Response**  
After the request is processed, you will receive a response similar to this:

 ```
{  
	"status": "SUCCESS",  
	"message": "Top 5 words fetched successfully",  
	"body": {  
		"the": 11498,  
		"and": 8607,  
		"I": 7217,  
		"to": 6609,  
		"of": 6423  
	}  
}
```