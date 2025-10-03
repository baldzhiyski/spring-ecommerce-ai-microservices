package org.baldzhiyski.mcpserverecommerce.response;

public record ApiResponse<T>(String status, String message, T data) {}
