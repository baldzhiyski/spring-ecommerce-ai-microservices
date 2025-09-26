package org.baldzhiyski.notification.service;

import java.util.Map;

public interface TemplateRegistry {
    /** returns template name (without .html), e.g., "order-created" */
    String templateFor(String eventTypeOrRoutingKey);

    /** builds a subject using payload data (map or JsonNode) */
    String subjectFor(String eventTypeOrRoutingKey, Map<String, Object> model);
}
