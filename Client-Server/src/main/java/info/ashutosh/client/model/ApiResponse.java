package info.ashutosh.client.model;

public class ApiResponse<T> {

    private String status;
    private String message;
    private T body;

    public ApiResponse(String status, String message, T body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getBody() {
        return body;
    }
}
