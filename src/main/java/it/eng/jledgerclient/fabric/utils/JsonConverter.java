package it.eng.jledgerclient.fabric.utils;


import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import it.eng.jledgerclient.exception.JLedgerClientException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.logging.Logger;


public class JsonConverter {
    private final static Logger log = Logger.getLogger(JsonConverter.class.getName());

    public static String convertToJson(Object obj) throws JLedgerClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE); //This property put data in upper camel case
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new JLedgerClientException(e);
        }
    }

    public static String convertToJsonNode(Object obj) throws JLedgerClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new JLedgerClientException(e);
        }
    }

    public static Object convertFromJson(String json, Class clazz, boolean isCollection) throws JLedgerClientException {
        try {
            if (StringUtils.isEmpty(json))
                throw new JLedgerClientException("Json data is EMPTY");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true); //This property serialize/deserialize not considering the case of fields
            if (isCollection) {
                //ObjectReader objectReader = mapper.reader().forType(new TypeReference<List<?>>() {
                //});
                // return objectReader.readValue(json);
                return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new JLedgerClientException(e);
        }
    }


}


